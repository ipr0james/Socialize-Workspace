package net.thenova.socialize.games.blackjack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.games.card.Card;
import net.thenova.socialize.util.response.ResponseMessage;

import java.util.List;
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
public final class BlackjackGame {

    private final List<Card> deck = Card.deck();
    private final String blank;
    private final EmbedBuilder builder;

    private final BlackjackPlayer dealer = new BlackjackPlayer(null);

    private final BlackjackPlayer player;
    private final Entity entity;

    private final long bet;
    private Message message;

    public BlackjackGame(final CommandContext context, final long bet) {
        this.player = new BlackjackPlayer(context.getMember().getUser().getIdLong());
        this.entity = EntityHandler.INSTANCE.getEntity(context.getMember());

        this.bet = bet;

        this.blank = "<:hidden:" + Bot.getJDA().getEmoteById(606785444444897292L).getIdLong() + ">";
        this.builder = Embed.casino(context.getMember());
        this.builder.appendDescription("Options:\n- `hit` - draw another card\n- `stand` - pass\n" + Embed.Z);

        this.dealCards(this.dealer);
        this.dealCards(this.player);

        this.displayCards();

        context.reply(builder.build()).queue(msg -> {
            this.message = msg;

            if(this.player.getCardValues(true) == 21) {
                this.end(GameState.WON_PLAYER_FIRST);
                this.message.editMessage(builder.build()).queue();
                return;
            }
            final boolean[] message = {false};

            ResponseMessage.create(msg, context.getMember(), response -> {

                switch(response.getContentRaw().toLowerCase()) {
                    case "hit":
                        final Card card = deck.remove(0);

                        if(this.player.hit(card)) {
                            this.end(GameState.WON_DEALER);
                        } else if(this.player.getCardValues(true) == 21) {
                            this.end(GameState.WON_PLAYER);
                        } else {
                            this.displayCards();
                        }
                        break;
                    case "stand":
                        int dealerValue = this.dealer.getCardValues(true);
                        boolean playerWon = false;

                        while(dealerValue < 17) {
                            if (this.dealer.hit(this.deck.remove(0))) {
                                playerWon = true;
                            }

                            dealerValue = this.dealer.getCardValues(true);
                        }

                        if(playerWon || this.player.getCardValues(true) > dealerValue) {
                            this.end(GameState.WON_PLAYER);
                        } else if(this.player.getCardValues(true) == dealerValue) {
                            this.end(GameState.LOSE);
                        } else {
                            this.end(GameState.WON_DEALER);
                        }
                        break;
                    default:
                        if(message[0]) {
                           return;
                        }
                        context.error("You are still participating in Blackjack, you must finish this before continuing.");
                        message[0] = true;
                        return;
                }

                this.message.editMessage(this.builder.build()).queue();
            });
        });
    }

    private void end(GameState state) {
        this.displayCards(state);

        switch(state) {
            case WON_PLAYER:
            case WON_PLAYER_FIRST:
                this.builder.setColor(Embed.EmbedColor.GREEN.get());
                this.builder.setDescription(Embed.Z + "\nYou have won `"
                        + Math.floor(this.bet * (state == GameState.WON_PLAYER ? 2 : 2.5))
                        + "`" + Bot.INSTANCE.getEmoji("coin"));
                this.entity.getBalance().add((long) (this.bet + (state == GameState.WON_PLAYER ? 0 : Math.floor(this.bet * 0.5))), EntityBalance.Reason.CASINO);
                break;
            case LOSE:
                this.builder.setColor(Embed.EmbedColor.RED.get());
                this.builder.setDescription(Embed.Z + "\nNo winner.");
                break;
            case WON_DEALER:
                this.builder.setColor(Embed.EmbedColor.RED.get());
                this.builder.setDescription(Embed.Z + "\nThe dealer won. You lost `" + this.bet + "`" + Bot.INSTANCE.getEmoji("coin"));
                this.entity.getBalance().take(this.bet, EntityBalance.Reason.CASINO);
        }

        this.builder.appendDescription("\n" + Embed.Z);

        ResponseMessage.remove(this.message.getIdLong());
    }

    private void displayCards() {
        this.displayCards(GameState.INPROGRESS);
    }

    private void displayCards(GameState state) {
        this.builder.clearFields();

        this.builder.addField(state == GameState.WON_DEALER ? "**You Bust**" : "**Your Hand**",
                this.player.getCards().stream().map(Card::getEmoji).collect(Collectors.joining())
                        + "\nCard Value: **" + this.player.getCardValues(true) + "**",
                true);
        this.builder.addField(state == GameState.WON_PLAYER ? "**Dealer Bust**" : "**Dealer Hand**",
                this.dealer.getCards().stream()
                            .map(card -> state == GameState.INPROGRESS && card.isHiddenDealer() ? this.blank : card.getEmoji())
                            .collect(Collectors.joining())
                        + "\nCard Value: **" + this.dealer.getCardValues(state != GameState.INPROGRESS) + "**",
                true);
    }

    /**
     * Deals the cards to the player.
     * @param player The player.
     */
    private void dealCards(BlackjackPlayer player) {
        for(int i = 0; i < 2; i++) {
            Card card = deck.remove(0);
            if(i == 1 && player.getID() == null) {
                card.setHiddenDealer(true);
            }
            player.hit(card);
        }
    }

    private enum GameState {
        INPROGRESS,
        WON_PLAYER_FIRST,
        WON_PLAYER,
        WON_DEALER,
        LOSE
    }

}
