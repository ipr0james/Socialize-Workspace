package net.thenova.socialize.command.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.games.card.Card;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        usage = "war <bet>",
        description = "Tag another member to play war with them."
)
public final class CommandWar extends Command {

    public CommandWar() {
        super("war");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
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

        final EmbedBuilder builder = Embed.casino(context.getMember());
        builder.setTitle("War");

        builder.appendDescription("Dealing cards...");

        context.reply(builder.build()).queue(message -> {
            Executors.newSingleThreadScheduledExecutor()
                    .schedule(() -> this.draw(entity, context.getMember(), bet, message),
                            3, TimeUnit.SECONDS);
        });
    }

    private void draw(final Entity entity, final Member member, final long bet, final Message message) {
        final List<Card> deck = Card.deck();
        final Card player = deck.remove(0);
        final Card dealer = deck.remove(0);

        if(player.getType().getValue() == dealer.getType().getValue()) {
            this.draw(entity, member, bet, message);
            return;
        }

        final EmbedBuilder builder = Embed.casino(member);
        builder.setTitle("War");
        final boolean winner = player.getType().getValue() > dealer.getType().getValue();

        if(winner) {
            builder.setColor(Embed.EmbedColor.GREEN.get());
            builder.setDescription("\nYou have won " + (bet * 2) + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z);
            entity.getBalance().add(bet, EntityBalance.Reason.CASINO);
        } else {
            builder.setColor(Embed.EmbedColor.RED.get());
            builder.setDescription("\nYou have lost " + (bet) + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z);
            entity.getBalance().take(bet, EntityBalance.Reason.CASINO);
        }

        builder.addField( "**Your Card**", player.getEmoji() + "\nCard Value: **" + player.getType().getValue() + "**", true);
        builder.addField( "**Dealer Card**", dealer.getEmoji() + "\nCard Value: **" + dealer.getType().getValue() + "**", true);

        message.editMessage(builder.build()).queue();


    }
}

