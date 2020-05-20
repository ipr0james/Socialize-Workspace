package net.thenova.socialize.command.commands.system.level.prestige.subs_prestige;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.level.prestige.PrestigeManager;

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
        usage = "system level prestige info <level>",
        description = "View information about a the prestige"
)
public final class CommandSystemLevelPrestigeInfo extends Command {

    public CommandSystemLevelPrestigeInfo() {
        super("info");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final long prestige;
        try {
            prestige = Long.parseLong(context.getArgument(0));
        } catch (final NumberFormatException ex) {
            context.error("Prestige must be numerical.");
            return;
        }

        final long max;
        if((max = PrestigeManager.INSTANCE.getMaxPrestige(entity.getGuildID())) > prestige) {
            context.error("The maximum prestige is " + max + ".");
            return;
        }

        Set<Role> roles = PrestigeManager.INSTANCE.getPrestigeRoles(context.getGuild().getIdLong(), prestige);

        final EmbedBuilder embed = Embed.socialize();
        embed.setTitle("Prestige Information: " + prestige);
        embed.addBlankField(true);
        embed.addField("Roles", roles.stream()
                .map(role -> "<@" + role.getIdLong() + " (" + role.getIdLong() + ")")
                .collect(Collectors.joining(", ")), true);

        context.reply(embed).queue();
    }
}
