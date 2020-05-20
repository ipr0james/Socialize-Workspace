package net.thenova.socialize.command.commands.system.games;

import net.thenova.socialize.command.CommandTemplateHelp;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.system.games.subs_games.CommandSystemGamesLottery;

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
        usage = "system games [args]",
        description = "Change settings for the bot games"
)
public class CommandSystemGames extends CommandTemplateHelp {

    public CommandSystemGames() {
        super("games");

        this.addSubCommand(
                new CommandSystemGamesLottery()
        );
    }
}
