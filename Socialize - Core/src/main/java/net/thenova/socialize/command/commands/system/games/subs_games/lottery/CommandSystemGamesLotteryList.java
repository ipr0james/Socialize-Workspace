package net.thenova.socialize.command.commands.system.games.subs_games.lottery;

import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.data.entries.games.GameLotteryData;

import java.util.Arrays;
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
        min = 0,
        usage = "system games lottery list",
        description = "List the available channel types."
)
public final class CommandSystemGamesLotteryList extends Command {

    public CommandSystemGamesLotteryList() {
        super("list");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        context.reply(Embed.socialize().appendDescription("Valid types: " + Arrays.stream(GameLotteryData.Type.values())
                .map(Enum::name)
                .collect(Collectors.joining(", ")))).queue();
    }
}
