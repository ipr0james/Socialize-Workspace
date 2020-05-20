package net.thenova.socialize.command.commands.games.subs_lottery;

import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.games.GameManager;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.games.GameLotteryData;
import net.thenova.titan.library.util.UNumber;

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
        usage = "lottery buy <amount>",
        description = "Purchase an amount of lottery tickets."
)
public final class SubCommandLotteryBuy extends Command {

    public SubCommandLotteryBuy() {
        super("buy");
    }

    /**
     * Execution method for the command once 'run' has completed execution checks.
     *
     * @param entity
     * @param context - Commands context
     */
    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final long current = GameManager.INSTANCE.getLotteryData().getTickets(entity.getGuildID()).getOrDefault(entity.getUserID(), 0L);
        final long max = GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GameLotteryData()).get(GameLotteryData.Type.TICKET_MAX).asLong();
        if(current >= max) {
            context.error("You have the maximum number of lottery tickets.");
            return;
        }

        if(!UNumber.isLong(context.getArgument(0))) {
            context.error("You must enter a numerical amount of tickets.");
            return;
        }

        long tickets = Long.parseLong(context.getArgument(0));
        if(context.getArguments().length > 0 && current + tickets > max) {
            context.error("Buying this many tickets will exceed the maximum tickets.\n" + Embed.Z +
                    "\n**Current:** `" + current
                    + "`\n**Maximum:** `" + max + "`\n" + Embed.Z);
            return;
        }

        final long defaultPrice = GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GameLotteryData()).get(GameLotteryData.Type.TICKET_PRICE).asLong();
        final long price = tickets * defaultPrice;
        if(price > entity.getBalance().fetch()) {
            context.error("You do not have enough coins to purchase this many tickets.\n" + Embed.Z +
                    "\n**Ticket Cost:** `" + defaultPrice + "`\n" + Embed.Z);
            return;
        }

        GameManager.INSTANCE.getLotteryData().addTicket(entity, tickets);
        entity.getBalance().take(price, EntityBalance.Reason.SYSTEM);

        context.reply(Embed.casino(context.getMember())
                .appendDescription("You have purchased `" + tickets + "` ticket" + (tickets > 1 ? "s" : "") + " for the daily lottery.")
        ).queue();
    }
}
