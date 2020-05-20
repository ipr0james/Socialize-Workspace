package net.thenova.socialize.command.commands.gang.leader;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.util.response.ResponseReaction;

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
@CommandUsage(
        min = 0,
        usage = "gang disband",
        description = "Disband the gang (Leader only)"
)
public final class SubCommandGangDisband extends CommandTemplateGang {

    public SubCommandGangDisband() {
        super("disband", GangRole.LEADER);
    }

    @Override
    public final void run(final Gang gang, final Entity entity, final CommandContext context) {
        final EmbedBuilder builder = Embed.gang(context.getMember());

        builder.setTitle("Disband Gang: **" + gang.getData().fetch(GangData.Type.NAME) + "**");
        builder.appendDescription("Disbanding will delete all coins and upgrades.\n" + Embed.Z + "\nAre you sure?");

        context.reply(builder).queue(message -> {
            ResponseReaction.create(message, context.getMember(), false)
                    .reaction(Bot.INSTANCE.getEmoji("gang_tick"), action -> {
                        if(gang.getMembers().fetch(entity.getUserID()).getRole() != GangRole.LEADER) {
                            message.editMessage(Embed.error(context.getMember(), "You are not the Gang Leader.")).queue();
                        } else {
                            GangManager.INSTANCE.disband(gang);
                            message.editMessage(Embed.gang(context.getMember())
                                    .setTitle("Gang Disbanded.")
                                    .appendDescription("Your gang has now been disbanded.")
                                    .build())
                                    .queue();                        }

                        ResponseReaction.remove(message.getIdLong());
                    })
                    .reaction(Bot.INSTANCE.getEmoji("gang_cross"), action -> {
                        ResponseReaction.remove(message.getIdLong());
                        message.editMessage(Embed.gang(context.getMember())
                                .setTitle("Disband Cancelled!")
                                .appendDescription("Gang disband has been cancelled.")
                                .build())
                                .queue();
                    })
                    .cancellable(60, () -> message.editMessage(Embed.error(context.getMember(), "Action has timed out.")).queue());
        });
    }
}
