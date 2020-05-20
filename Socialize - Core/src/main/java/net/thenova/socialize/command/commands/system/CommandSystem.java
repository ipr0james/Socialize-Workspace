package net.thenova.socialize.command.commands.system;

import net.dv8tion.jda.api.Permission;
import net.thenova.socialize.command.CommandTemplateHelp;
import net.thenova.socialize.command.commands.system.channels.CommandSystemChannel;
import net.thenova.socialize.command.commands.system.games.CommandSystemGames;
import net.thenova.socialize.command.commands.system.group.CommandSystemCommandGroup;
import net.thenova.socialize.command.commands.system.level.CommandSystemLevel;
import net.thenova.socialize.command.commands.system.roles.CommandSystemRole;
import net.thenova.socialize.command.permission.CommandPermission;

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
public final class CommandSystem extends CommandTemplateHelp {

    public CommandSystem() {
        super("system", "sys");

        super.addPermission(
                CommandPermission.discord(Permission.ADMINISTRATOR)
        );

        super.addSubCommand(
                new CommandSystemStart(),
                new CommandSystemGames(),
                new CommandSystemChannel(),
                new CommandSystemCommandGroup(),
                new CommandSystemLevel(),
                new CommandSystemRole()
        );
    }
}
