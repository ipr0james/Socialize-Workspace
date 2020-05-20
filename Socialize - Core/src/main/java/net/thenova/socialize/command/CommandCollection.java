package net.thenova.socialize.command;

import lombok.Getter;
import net.thenova.socialize.command.commands.CommandHelp;
import net.thenova.socialize.command.commands.CommandTest;
import net.thenova.socialize.command.commands.economy.CommandCoins;
import net.thenova.socialize.command.commands.economy.CommandPay;
import net.thenova.socialize.command.commands.games.CommandCoinFlip;
import net.thenova.socialize.command.commands.games.CommandDuel;
import net.thenova.socialize.command.commands.games.CommandLottery;
import net.thenova.socialize.command.commands.games.CommandWar;
import net.thenova.socialize.command.commands.gang.CommandGang;
import net.thenova.socialize.command.commands.statistics.CommandExperience;
import net.thenova.socialize.command.commands.statistics.CommandLeaderboard;
import net.thenova.socialize.command.commands.statistics.CommandLevel;
import net.thenova.socialize.command.commands.statistics.CommandStatistics;
import net.thenova.socialize.command.commands.system.CommandEval;
import net.thenova.socialize.command.commands.system.CommandShutdown;
import net.thenova.socialize.command.commands.system.CommandSystem;

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
public enum CommandCollection {
    HELP(new CommandHelp()),

    ECONOMY_COINS(new CommandCoins()),
    ECONOMY_PAY(new CommandPay()),

    GAMES_COINFLIP(new CommandCoinFlip()),
    GAMES_DUEL(new CommandDuel()),
    GAMES_LOTTERY(new CommandLottery()),
    GAMES_WAR(new CommandWar()),

    GANG(new CommandGang()),

    STATISTICS_EXPERIENCE(new CommandExperience()),
    STATISTICS_LEADERBOARD(new CommandLeaderboard()),
    STATISTICS_LEVEL(new CommandLevel()),
    STATISTICS_STATISTICS(new CommandStatistics()),

    TEST(new CommandTest()),

    SYSTEM_EVAL(new CommandEval()),
    SYSTEM_SHUTDOWN(new CommandShutdown()),
    SYSTEM(new CommandSystem());

    @Getter private final Command command;

    CommandCollection(final Command command) {
        this.command = command;
    }
}
