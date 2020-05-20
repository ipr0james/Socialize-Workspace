package net.thenova.socialize.command.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.titan.library.util.URandom;

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
        usage = "coinflip <bet> <h/t>",
        description = "Toss a coin and try to win the bet"
)
public final class CommandCoinFlip extends Command {

    private final String coin;

    public CommandCoinFlip() {
        super("coinflip", "cf");

        this.coin = Bot.INSTANCE.getEmoji("coins");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final String value = context.getArgument(1);
        if(!(value.equalsIgnoreCase("h")
                || value.equalsIgnoreCase("t")
                || value.equalsIgnoreCase("heads")
                || value.equalsIgnoreCase("tails"))) {
            context.error("You must enter either `heads` or `tails`.");
            return;
        }

        boolean entry = value.equals("h") || value.equalsIgnoreCase("heads");
        long bet;

        try {
            bet = Long.parseLong(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Your bet must be a number.");
            return;
        }

        if(bet < 1) {
            context.error("The minimum bet is `1` " + this.coin);
            return;
        }


        if(entity.getBalance().fetch() < bet) {
            context.error("You do not have enough coins to bet this much.");
            return;
        }

        final boolean heads = URandom.nextBoolean();
        final EmbedBuilder builder = Embed.casino(context.getMember());

        builder.setTitle("Coin Flip");
        builder.setColor(Embed.EmbedColor.GREEN.get());

        builder.appendDescription("The coin flips and lands on..." +
                "\n**" + (heads ? "heads" : "tails") + "**" +
                "\n" + Embed.Z + "\n");

        if((heads && entry) || (!heads && !entry)) {
            builder.appendDescription("You won and received `" + (bet * 2));
            entity.getBalance().add(bet, EntityBalance.Reason.CASINO);
        } else {
            builder.setColor(Embed.EmbedColor.RED.get());
            builder.appendDescription("You lost `" + bet);
            entity.getBalance().take(bet, EntityBalance.Reason.CASINO);
        }

        builder.appendDescription("`" + this.coin);

        context.reply(builder.build()).queue();
    }
}

