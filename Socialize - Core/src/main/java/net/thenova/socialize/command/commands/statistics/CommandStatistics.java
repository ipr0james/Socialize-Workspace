package net.thenova.socialize.command.commands.statistics;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityMultiplier;
import net.thenova.socialize.entities.modules.EntityStats;
import net.thenova.socialize.entities.modules.stat_keys.CountText;
import net.thenova.socialize.entities.modules.stat_keys.CountVoice;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.titan.library.util.UNumber;

import java.text.DecimalFormat;
import java.util.List;

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
        usage = "statistics [user]",
        description = "Display a users current statistics."
)
public final class CommandStatistics extends Command {

    public CommandStatistics() {
        super("statistics", "stats");
    }

    @Override
    protected void execute(final Entity entity, final CommandContext context) {
        final List<Member> mentioned = context.getMessage().getMentionedMembers();

        if(mentioned.isEmpty()) {
            this.sendEmbed(context, context.getMember());
        } else {
            this.sendEmbed(context, mentioned.get(0));
        }
    }

    private void sendEmbed(CommandContext context, Member member) {
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);
        if(entity == null) {
            context.error("Invalid user provided.");
            return;
        }

        final EntityStats data = entity.getStats();
        final EmbedBuilder builder = Embed.def();
        builder.setAuthor(member.getEffectiveName() + (member.getEffectiveName().endsWith("s") ? "'" : "'s") + " statistics",
                null,
                member.getUser().getEffectiveAvatarUrl());
        builder.setThumbnail("https://cdn.discordapp.com/emojis/601397100227461120.png");
        builder.setFooter("Earn xp/coins by being active", null);

        final DecimalFormat format = new DecimalFormat("0.0");

        builder.addField("**Gang**", (data.fetch(new GangID()) == -1L ? "`N/A`" : "`" + GangManager.INSTANCE.getGang(entity.getGuildID(), data.fetch(new GangID())).getData().fetch(GangData.Type.NAME).toString() + "`") + "\n" + Embed.Z, false);
        builder.addField("**XP Multiplier**", "`" + format.format(entity.getMultiplier().fetch(EntityMultiplier.Type.EXPERIENCE)) + "x`" + "\n" + Embed.Z, true);
        builder.addField("**Coin Multiplier**", "`" + format.format(entity.getMultiplier().fetch(EntityMultiplier.Type.COINS)) + "x`" + "\n" + Embed.Z, true);
        builder.addBlankField(true);
        builder.addField("**Text Chat**", "`" + UNumber.format(data.fetch(new CountText())) + " minutes`", true);
        builder.addField("**Voice Chat**", "`" + UNumber.format(data.fetch(new CountVoice())) + " minutes`", true);

        context.reply(builder).queue();
    }
}
