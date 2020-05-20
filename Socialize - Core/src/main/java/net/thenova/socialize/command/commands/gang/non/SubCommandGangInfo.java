package net.thenova.socialize.command.commands.gang.non;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.member.GangMembers;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeBank;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeMembers;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeTaxDecrease;
import net.thenova.titan.library.util.UNumber;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

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
        usage = "gang info [name]",
        description = "View info about your own or another gang."
)
public final class SubCommandGangInfo extends CommandTemplateGang {

    public SubCommandGangInfo() {
        super("info", GangRole.NON, "information");
    }

    @Override
    public final void run(Gang gang, final Entity entity, final CommandContext context) {
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

        SubCommandGangInfo.sendEmbed(context, gang);
    }

    public static void sendEmbed(final CommandContext context, final Gang gang) {
        final EmbedBuilder builder = Embed.gang(context.getMember());
        final GangMembers members = gang.getMembers();

        builder.setTitle("**Gang - " + gang.getData().fetch(GangData.Type.NAME) + "**");
        builder.setAuthor(null, null, null);

        final DecimalFormat format = new DecimalFormat("0.0");

        builder.addField("**XP Multiplier**", "`" + format.format((double) gang.getData().fetch(GangData.Type.MULTIPLIER_EXPERIENCE) + 1.0) + "x`"
                + Bot.INSTANCE.getEmoji("blank") + "\n" + Embed.Z, true);
        builder.addField("**Coin Multiplier**", "`" + format.format((double) gang.getData().fetch(GangData.Type.MULTIPLIER_COINS) + 1.0) + "x`"
                + Bot.INSTANCE.getEmoji("blank") + "\n" + Embed.Z, true);

        final long coins = gang.getBank().fetch();
        if(coins < 0) {
            builder.setColor(Embed.EmbedColor.RED.get());
        }

        builder.addField("**Bank**", "`" + UNumber.format(coins) + " / "
                + UNumber.format(((GangUpgradeBank) gang.getUpgrades().fetch(GangUpgradeType.BANK)).getMaxBalance()) + "`"
                + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z, true);

        final GangUpgradeTaxDecrease tax = (GangUpgradeTaxDecrease) gang.getUpgrades().fetch(GangUpgradeType.TAX_DECREASE);
        builder.addField("**Daily Tax** (" + tax.getTaxDecrease(tax.getLevel()) +" per person)", "`" + tax.getTax() + "`" + Bot.INSTANCE.getEmoji("coins"), true);

        if((int) gang.getData().fetch(GangData.Type.OVERDRAWN) > 0) {
            builder.setFooter("WARNING | Your gang balance is overdrawn. Use `" + CommandMap.INSTANCE.getPrefix(context.getGuild().getIdLong()) + "gang tax` for info.", null);
        } else {
            builder.setFooter("Use `" + CommandMap.INSTANCE.getPrefix(context.getGuild().getIdLong()) + "gang shop` to purchase Gang upgrades.", null);
        }

        StringBuilder str = new StringBuilder();
        str.append("Leader - <@").append(members.getMembers(GangRole.LEADER).get(0).getUserID()).append(">\nOfficers - ");

        String collect = members.getMembers(GangRole.OFFICER).stream().map(member -> "<@" + member.getUserID() + ">").collect(Collectors.joining(" "));
        str.append(collect.isEmpty() ? "none" : collect);
        str.append("\nMembers - ");

        collect = members.getMembers(GangRole.MEMBER).stream().map(member -> "<@" + member.getUserID() + ">").collect(Collectors.joining(" "));
        str.append(collect.isEmpty() ? "none" : collect);
        str.append("\n" + Embed.Z);

        builder.addField("**Members** (" + members.getMembers().size() + "**/**" + ((GangUpgradeMembers) gang.getUpgrades().fetch(GangUpgradeType.MEMBERS)).getMaxMembers() + ")", str.toString(), false);

        context.reply(builder.build()).queue();
    }
}
