package net.thenova.socialize.command.commands.system.games.subs_games;

import net.thenova.socialize.command.CommandTemplateHelp;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.system.games.subs_games.lottery.CommandSystemGamesLotteryInfo;
import net.thenova.socialize.command.commands.system.games.subs_games.lottery.CommandSystemGamesLotteryList;
import net.thenova.socialize.command.commands.system.games.subs_games.lottery.CommandSystemGamesLotterySet;

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
        usage = "system games lottery [args]",
        description = "Change settings for the game lottery"
)
public final class CommandSystemGamesLottery extends CommandTemplateHelp {

    public CommandSystemGamesLottery() {
        super("lottery");

        this.addSubCommand(
                new CommandSystemGamesLotteryInfo(),
                new CommandSystemGamesLotteryList(),
                new CommandSystemGamesLotterySet()
        );
    }
}
