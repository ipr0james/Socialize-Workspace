package net.thenova.socialize.gangs;

import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangDMs;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.member.GangMember;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeTaxDecrease;
import net.thenova.titan.library.Titan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class TaskGangUpkeep implements Runnable {

    private final long guildID;

    public TaskGangUpkeep(long guildID) {
        this.guildID = guildID;
    }

    @Override
    public void run() {
        final Collection<Gang> gangs = GangManager.INSTANCE.getGangs().get(guildID).values();

        gangs.forEach(gang -> {
            final GangData data = gang.getData();
            final long price = ((GangUpgradeTaxDecrease) gang.getUpgrades().fetch(GangUpgradeType.TAX_DECREASE)).getTax();
            gang.getBank().take(price);

            final long balance = gang.getBank().fetch();

            Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Took {} from {}.", price, data.fetch(GangData.Type.NAME));

            final int overdrawn = (int) data.fetch(GangData.Type.OVERDRAWN);
            if(balance < 0) {
                data.set(GangData.Type.OVERDRAWN, overdrawn + 1);

                final List<GangMember> toMessage = new ArrayList<>();
                toMessage.addAll(gang.getMembers().getMembers(GangRole.LEADER));
                toMessage.addAll(gang.getMembers().getMembers(GangRole.OFFICER));

                if(overdrawn < 3) {
                    toMessage.forEach(member -> this.sendOverdrawn(member, gang, balance));
                } else {
                    GangManager.INSTANCE.disband(gang);
                    gang.disband();

                    toMessage.forEach(this::sendDisbanded);
                    Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Gang {} has been disbanded as they were overdrawn ({} : ${})",
                            gang.getData().fetch(GangData.Type.NAME), gang.getData().fetch(GangData.Type.OVERDRAWN), balance);
                }
            } else if(overdrawn > 0) {
                gang.getData().set(GangData.Type.OVERDRAWN, 0);
            }
        });
    }

    private void sendOverdrawn(final GangMember gmember, final Gang gang, final long balance) {
        if(!gmember.fetch(new GangDMs())) {
            return;
        }

        final Member member = Objects.requireNonNull(gmember.getEntity()).getMember();
        assert member != null;

        member.getUser().openPrivateChannel().queue(res -> {
            final int overdrawn = (int) gang.getData().fetch(GangData.Type.OVERDRAWN);

            res.sendMessage(Embed.gang(member)
                    .setColor(Embed.EmbedColor.RED.get())
                    .appendDescription("Your gang balance is overdrawn by "
                            + balance + Bot.INSTANCE.getEmoji("coins")
                            + " and is " + (3 - overdrawn) + " day" + (overdrawn == 1 ? "" : "s")
                            + " from being disbanded.")
                    .build())
                    .queue(rs -> {}, throwable -> {
                        throwable.printStackTrace();
                        Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Failed to send private message to ({}:  {})",
                                member.getEffectiveName(),
                                member.getUser().getIdLong());
                    });
        }, throwable -> {
            throwable.printStackTrace();
            Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Failed to open private channel with ({}:  {})",
                    member.getEffectiveName(),
                    member.getUser().getIdLong());
        });
    }

    private void sendDisbanded(final GangMember gmember) {
        if(!gmember.fetch(new GangDMs())) {
            return;
        }

        final Member member = Objects.requireNonNull(gmember.getEntity()).getMember();
        assert member != null;

        member.getUser().openPrivateChannel().queue(res -> {
            res.sendMessage(Embed.gang(member)
                    .setColor(Embed.EmbedColor.RED.get())
                    .appendDescription("Your gang has been disbanded as it was overdrawn for 3 days.")
                    .build())
                    .queue(rs -> {}, throwable -> {
                        throwable.printStackTrace();
                        Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Failed to send private message to (for disband) ({}:  {})",
                                member.getEffectiveName(),
                                member.getUser().getIdLong());
                    });
        }, throwable -> {
            throwable.printStackTrace();
            Titan.INSTANCE.getDebug().info("[TaskGangUpkeep] - Failed to open private channel with (for disband) ({}:  {})",
                    member.getEffectiveName(),
                    member.getUser().getIdLong());
        });
    }
}
