package net.thenova.socialize.command.commands.system.level.prestige.subs_prestige;

import de.arraying.kotys.JSONArray;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.level.prestige.LevelPrestigeRolesData;

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
        min = 2,
        usage = "system level prestige removerole {level} {role_id}",
        description = "Remove a role from a given level"
)
public final class CommandSystemLevelPrestigeRemoveRole extends Command {

    public CommandSystemLevelPrestigeRemoveRole() {
        super("removerole", "remove");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final int levelNum;
        try {
            levelNum = Integer.parseInt(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Prestige must be numerical.");
            return;
        }

        if(levelNum < 0) {
            context.error("Invalid prestige provided. Prestige must be greater than 0.");
            return;
        }

        final long roleID;
        try {
            roleID = Long.parseLong(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Role ID must be numerical.");
            return;
        }

        final LevelPrestigeRolesData data = GuildHandler.INSTANCE.fetch(context.getGuild().getIdLong(), new LevelPrestigeRolesData());
        final JSONArray array = data.contains(String.valueOf(levelNum)) ? data.get(String.valueOf(levelNum)).asArray() : new JSONArray();

        for(int i = 0; i < array.length(); i++) {
            if(array.large(i).equals(roleID)) {
                array.delete(i);
                data.set(String.valueOf(levelNum), array);
                context.reply(Embed.socialize("The role <@" + roleID + "> has been removed from Prestige " + levelNum + ", all current users will have this role removed."));
                return;
            }
        }

        context.error("The role <@" + roleID + "> is not a part of Level " + levelNum);
    }
}