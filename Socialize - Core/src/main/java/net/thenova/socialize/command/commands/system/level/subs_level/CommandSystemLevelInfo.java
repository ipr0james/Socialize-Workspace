package net.thenova.socialize.command.commands.system.level.subs_level;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.level.Level;
import net.thenova.socialize.level.LevelManager;

import java.util.Set;
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
        min = 1,
        usage = "system level info <level>",
        description = "View information about a given level"
)
public final class CommandSystemLevelInfo extends Command {

    public CommandSystemLevelInfo() {
        super("info");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final int levelNum;
        try {
            levelNum = Integer.parseInt(context.getArgument(0));
        } catch (final NumberFormatException ex) {
            context.error("Level must be numerical.");
            return;
        }

        final Level level;
        try {
            level = LevelManager.INSTANCE.getLevel(levelNum);
        } catch (final IndexOutOfBoundsException ex) {
            context.error("Invalid level provided. Levels go between 0-100");
            return;
        }

        long req = level.getRequirement();
        Level prev = level;
        while((prev = prev.getPreviousLevel()) != null) {
            req = req - prev.getRequirement();
        }

        Set<Role> roles = LevelManager.INSTANCE.getLevelRoles(context.getGuild().getIdLong(), levelNum);

        final EmbedBuilder embed = Embed.socialize();
        embed.setTitle("Level Information: " + levelNum);
        embed.addField("XP Requirement", req + "", true);
        embed.addField("Total XP Requirement", level.getRequirement() + "", true);
        embed.addBlankField(true);
        embed.addField("Roles", roles.stream()
                .map(role -> "<@" + role.getIdLong() + " (" + role.getIdLong() + ")")
                .collect(Collectors.joining(", ")), true);

        context.reply(embed).queue();
    }
}
