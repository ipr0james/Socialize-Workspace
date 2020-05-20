package net.thenova.socialize.command.commands.statistics;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityLevel;
import net.thenova.socialize.entities.modules.stat_keys.experience.PrestigeCurrent;
import net.thenova.socialize.level.Level;

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
        usage = "level [user]",
        description = "Display a users current level."
)
public final class CommandLevel extends Command {

    public CommandLevel() {
        super("level", "lvl", "rank");
    }

    @Override
    protected void execute(Entity entity, CommandContext context) {
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

        final EntityLevel level = entity.getLevel();

        final Level current = level.getLevel();
        final Level next = level.getLevel().getNextLevel();

        final long currentXP = level.getXP() - current.getRequirement();
        final String maxXP = next == null ? "Max" : next.getRequirement() - current.getRequirement() + "";
        final String reqXP = next == null ? "N/A" : ((next.getRequirement() - current.getRequirement()) - currentXP) + "";

        final EmbedBuilder builder = Embed.def().setDescription("");
        builder.setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
        builder.setThumbnail("https://cdn.discordapp.com/attachments/586252610680520711/601482269718478849/graph.png");

        builder.addField("**Prestige " + entity.getStats().fetch(new PrestigeCurrent()) + "**", "", true);
        builder.addField("**Level " + current.getLevel() + "**", "", true);
        builder.addField("**Progress:**", currentXP + " **/** " + maxXP + " **(" + reqXP + ")**", false);

        context.reply(builder.build()).queue();
    }
}
