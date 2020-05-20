package net.thenova.socialize.command.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.games.roulette.RouletteGame;
import net.thenova.socialize.games.roulette.bet.RouletteBet;
import net.thenova.socialize.games.roulette.bet.RouletteBetType;

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
        usage = "roulette <bet> <type>",
        description = "Start or join a game of Roulette"
)
public final class CommandRoulette extends Command {

    public CommandRoulette() {
        super("roulette");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        if(context.getArguments().length < 2) {
            final EmbedBuilder builder = Embed.casino(context.getMember());
            builder.setTitle("Roulette");
            builder.setThumbnail(null);

            builder.appendDescription("Usage: `" + CommandMap.INSTANCE.getPrefix(entity.getGuildID()) + "roulette <bet> <type>`\n" + Embed.Z);

            final String value = Arrays.stream(RouletteBetType.values())
                    .map(type -> "[x" + type.getMultiplier() + "] " + type.getDescription())
                    .collect(Collectors.joining("\n")) + "\n" + Embed.Z;

            builder.addField("Payout Multipliers:", value, false);

            builder.setImage("https://cdn.discordapp.com/attachments/506838906872922145/506897446471860234/HSFToI0.png");
            context.reply(builder.build()).queue();
            return;
        }

        // Handle bet
        final long bet;

        try {
            bet = Long.parseLong(context.getArgument(0));
        } catch (NumberFormatException ex) {
            context.error("Your bet must be a number.");
            return;
        }

        if(bet < 1) {
            context.error("The minimum bet is `1` " + Bot.INSTANCE.getEmoji("coins"));
            return;
        }

        if(entity.getBalance().fetch() < bet) {
            context.error("You do not have enough coins to bet this much.");
            return;
        }

        // Handle bet type

        final RouletteBetType type;
        final String arg = context.getArgument(1).toLowerCase();

        switch(arg) {
            case "red":
            case "black":
                type = RouletteBetType.COLOR;
                break;
            case "even":
            case "odd":
                type = RouletteBetType.FORM;
                break;
            case "1-18":
            case "19-36":
                type = RouletteBetType.HALF;
                break;
            case "1st":
            case "2nd":
            case "3rd":
                type = RouletteBetType.COLUMN;
                break;
            case "1-12":
            case "13-24":
            case "25-36":
                type = RouletteBetType.DOZEN;
                break;
            default:
                try {
                    int straight = Integer.parseInt(arg);

                    if(straight < 0 || straight > 36) {
                        context.error("Value of straight must be between 0-36.");
                        return;
                    }

                    type = RouletteBetType.STRAIGHT;
                } catch (NumberFormatException ex) {
                    context.error("Invalid type");
                    return;
                }
        }

        // Handle game
        RouletteGame.get(context.getChannel()).join(entity, context, new RouletteBet(bet, type, arg));
    }
}

