package net.thenova.socialize.command.commands.system.roles.subs_roles;

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
        usage = "system role set <type> <value>",
        description = "Set a value."
)
public final class CommandSystemRoleSet extends Command {

    public CommandSystemRoleSet() {
        super("set");
    }

    /**
     * Execution method for the command once 'run' has completed execution checks.
     *
     * @param entity - Entity
     * @param context - Commands context
     */
    @Override
    protected void execute(Entity entity, CommandContext context) {
        final GuildRoleData.Type type;
        try {
            type = GuildRoleData.Type.valueOf(context.getArgument(0));
        } catch (final IllegalArgumentException ex) {
            context.error("Invalid type, try list.");
            return;
        }

        final long value;
        try {
            value = Long.parseLong(context.getArgument(1));
        } catch (final NumberFormatException ex) {
            context.error("The value must be a long/integer.");
            return;
        }

        if(context.getGuild().getRoleById(value) == null) {
            context.error("Invalid role");
            return;
        }

        GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GuildRoleData()).set(type.toString(), value);
        context.reply(Embed.socialize()
                .appendDescription("Role `" + type.toString() + "` has been set to `" + value + "`")
        ).queue();
    }
}
