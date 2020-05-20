package net.thenova.socialize.command.commands.statistics;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.statistics.subs_experience.SubCommandExperienceGive;
import net.thenova.socialize.command.commands.statistics.subs_experience.SubCommandExperienceReset;
import net.thenova.socialize.command.commands.statistics.subs_experience.SubCommandExperienceSet;
import net.thenova.socialize.command.commands.statistics.subs_experience.SubCommandExperienceTake;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.titan.library.util.UNumber;

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
        usage = "experience [user]",
        description = "Display yours or another users XP."
)
public final class CommandExperience extends Command {

    public CommandExperience() {
        super("experience", "xp");

        this.addSubCommand(
                new SubCommandExperienceGive(),
                new SubCommandExperienceReset(),
                new SubCommandExperienceSet(),
                new SubCommandExperienceTake()
        );
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final List<Member> mentioned = context.getMessage().getMentionedMembers();

        if(mentioned.isEmpty()) {
            this.sendEmbed(context, context.getMember());
        } else {
            this.sendEmbed(context, mentioned.get(0));
        }
    }

    private void sendEmbed(final CommandContext context, final Member member) {
        final EmbedBuilder builder = Embed.def();
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);

        if(entity == null) {
            context.error("Invalid user provided.");
            return;
        }

        builder.setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
        builder.setThumbnail("https://cdn.discordapp.com/attachments/586252610680520711/601477044412350464/lightning-bolt.png");
        builder.setFooter("Earn xp by chatting or voice calling", null);

        builder.appendDescription("**XP** - " + UNumber.format(entity.getLevel().getXP()) + Bot.INSTANCE.getEmoji("xp"));

        context.reply(builder.build()).queue();
    }
}
