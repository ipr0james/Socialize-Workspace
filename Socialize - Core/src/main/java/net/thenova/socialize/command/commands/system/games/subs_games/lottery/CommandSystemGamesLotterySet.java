package net.thenova.socialize.command.commands.system.games.subs_games.lottery;

import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.games.GameLotteryData;

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
        usage = "system games lottery set <type> <value>",
        description = "Set a value."
)
public final class CommandSystemGamesLotterySet extends Command {

    public CommandSystemGamesLotterySet() {
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
        final GameLotteryData.Type type;
        try {
            type = GameLotteryData.Type.valueOf(context.getArgument(0));
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

        GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GameLotteryData()).set(type.toString(), value);
        context.reply(Embed.socialize()
                .appendDescription("Type `" + type.toString() + "` has been set to `" + value + "`")
        ).queue();
    }
}
