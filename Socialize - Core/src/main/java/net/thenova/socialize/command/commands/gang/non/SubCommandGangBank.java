package net.thenova.socialize.command.commands.gang.non;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeBank;
import net.thenova.titan.library.util.UNumber;

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
        usage = "gang bank [name]",
        description = "View the Bank information about your gang."
)
public final class SubCommandGangBank extends CommandTemplateGang {

    public SubCommandGangBank() {
        super("bank", GangRole.NON);
    }

    @Override
    public void run(Gang gang, final Entity entity, final CommandContext context) {
        if (context.getArguments().length < 1) {
            if(gang == null) {
                context.error("You are not part of a gang.");
                return;
            }
        } else {
            if (context.getMessage().getMentionedMembers().isEmpty()) {
                gang = GangManager.INSTANCE.getGangByName(entity.getGuildID(), String.join(" ", context.getArguments()));
                if (gang == null) {
                    context.error("Invalid Gang!");
                    return;
                }
            } else {
                final Entity mentioned = EntityHandler.INSTANCE.getEntity(context.getMessage().getMentionedMembers().get(0));
                if (mentioned == null) {
                    context.error("Invalid member");
                    return;
                }

                final long gangID;
                if ((gangID = mentioned.getStats().fetch(new GangID())) != -1) {
                    gang = GangManager.INSTANCE.getGang(entity.getGuildID(), gangID);
                } else {
                    context.error(mentioned.asMention() + " is not part of a gang.");
                    return;
                }
            }
        }

        final EmbedBuilder builder = Embed.gang(context.getMember());
        final long coins = gang.getBank().fetch();

        builder.setTitle("**Gang** - " + gang.getData().fetch(GangData.Type.NAME) + "");
        if(coins < 0) {
            builder.setColor(Embed.EmbedColor.RED.get());
        }
        builder.appendDescription("**Coins** - "
                + UNumber.format(coins).replace("-", "**-**") + " **/** "
                + UNumber.format(((GangUpgradeBank) gang.getUpgrades().fetch(GangUpgradeType.BANK)).getMaxBalance())
                + Bot.INSTANCE.getEmoji("coins"));

        builder.setFooter("Use `!gang deposit <amount>` to add funds.", null);

        context.reply(builder.build()).queue();
    }
}
