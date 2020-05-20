package net.thenova.socialize.command.commands.gang.member;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeTaxDecrease;

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
        usage = "gang tax",
        description = "View information about daily tax"
)
public final class SubCommandGangTax extends CommandTemplateGang {

    public SubCommandGangTax() {
        super("tax", GangRole.MEMBER);
    }

    @Override
    public final void run(final Gang gang, final Entity entity, final CommandContext context) {
        final EmbedBuilder builder = Embed.gang(context.getMember());
        builder.setTitle("Gang Tax Information");
        builder.appendDescription("Gang taxes are taken on a daily basis at a fee per person." +
                "\nIf the gang does not have sufficient funds they will become overdrawn." +
                "\nSpending 3 days overdrawn will result in being disbanded.\n" + Embed.Z);

        final GangUpgradeTaxDecrease tax = (GangUpgradeTaxDecrease) gang.getUpgrades().fetch(GangUpgradeType.TAX_DECREASE);
        final int overdrawn = (Integer) gang.getData().fetch(GangData.Type.OVERDRAWN);
        builder.addField("**Daily Tax** (" + tax.getTaxDecrease(tax.getLevel()) +" per person)",
                "`" + tax.getTax() + "`" + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z, false);

        builder.addField("Days Overdrawn", "`" + overdrawn + "`\n" + Embed.Z, true);

        final long balance = gang.getBank().fetch();

        builder.addField("Amount Overdrawn", balance < 0 ? "`" + balance + "`" + Bot.INSTANCE.getEmoji("coins") : "Not overdrawn", true);

        if(overdrawn > 0) {
            builder.setColor(Embed.EmbedColor.RED.get());
            builder.setFooter("WARNING | Gang is currently overdrawn and will be disbanded in "
                    + (3 - overdrawn) + "day" + (3 - overdrawn == 1 ? "" : "s"), null);
        } else {
            builder.setFooter("Your gang is not currently overdrawn", null);
        }

        context.reply(builder.build()).queue();
    }
}
