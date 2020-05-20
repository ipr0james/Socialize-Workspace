package net.thenova.socialize.command.commands.system.level.subs_level;

import de.arraying.kotys.JSONArray;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.level.LevelRolesData;

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
        usage = "system level addrole {level} {role_id}",
        description = "Add a role to a given level"
)
public final class CommandSystemLevelAddRole extends Command {

    public CommandSystemLevelAddRole() {
        super("addrole", "add");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final int levelNum;
        try {
            levelNum = Integer.parseInt(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Level must be numerical.");
            return;
        }

        if(levelNum < 0 || levelNum > 100) {
            context.error("Invalid level provided. Levels go between 0-100");
            return;
        }

        final long roleID;
        try {
            roleID = Long.parseLong(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Role ID must be numerical.");
            return;
        }

        if(context.getGuild().getRoleById(roleID) == null) {
            context.error("Invalid role ID provided.");
            return;
        }

        final LevelRolesData data = GuildHandler.INSTANCE.fetch(context.getGuild().getIdLong(), new LevelRolesData());
        final JSONArray array = data.contains(String.valueOf(levelNum)) ? data.get(String.valueOf(levelNum)).asArray() : new JSONArray();

        for(int i = 0; i < array.length(); i++) {
            if(array.large(i).equals(roleID)) {
                context.error("The role <@" + roleID + "> is already a part of Level " + levelNum);
                return;
            }
        }

        array.append(roleID);
        data.set(String.valueOf(levelNum), array);
        context.reply(Embed.socialize("Role <@" + roleID + "> has been added to level " + levelNum + ", all existing users will be updated."));
        //TODO - test?
    }
}
