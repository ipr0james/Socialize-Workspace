package net.thenova.socialize.command.commands.system.roles.subs_roles;

import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildRoleData;

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
        usage = "system role info <type>",
        description = "Get the current value of the type."
)
public final class CommandSystemRoleInfo extends Command {

    public CommandSystemRoleInfo() {
        super("info");
    }

    /**
     * Execution method for the command once 'run' has completed execution checks.
     *
     * @param entity
     * @param context - Commands context
     */
    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final GuildRoleData.Type type;
        try {
            type = GuildRoleData.Type.valueOf(context.getArgument(0).toUpperCase());
        } catch (final IllegalArgumentException ex) {
            context.error("Invalid type, try list.");
            return;
        }

        final GuildRoleData data = GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GuildRoleData());
        final Role role;
        if((role = data.getRole(type)) == null) {
            context.error("Type `" + type.toString() + "` has not been set.");
            return;
        }

        context.reply(Embed.socialize()
                .addField("Type", type.toString(), true)
                .addField("Value", role.getAsMention(), true)
        ).queue();
    }
}
