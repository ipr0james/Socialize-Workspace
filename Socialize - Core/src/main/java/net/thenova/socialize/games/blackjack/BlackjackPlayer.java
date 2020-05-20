package net.thenova.socialize.games.blackjack;

import net.thenova.socialize.games.card.Card;
import net.thenova.socialize.games.card.CardType;

import java.util.ArrayList;
import java.util.List;

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
public final class BlackjackPlayer {

    private final List<Card> cards = new ArrayList<>();
    private final Long id;

    /**
     * Creates a new blackjack player.
     * @param id The UUID of the member.
     */
    BlackjackPlayer(Long id) {
        this.id = id;
    }

    /**
     * Gets the cards of the player.
     * @return The cards.
     */
    List<Card> getCards() {
        return cards;
    }

    /**
     * Gets the ID of the player.
     * @return The id.
     */
    public Long getID() {
        return this.id;
    }

    /**
     * Hits the player with a card.
     * @param card The card.
     * @return True if the player got busted, false otherwise.
     */
    boolean hit(Card card) {
        cards.add(card);
        return getCardValues(true) > 21;
    }

    /**
     * Gets the value of the player's cards.
     * @return The value.
     */
    int getCardValues(boolean addHidden) {
        int sum = 0;
        int aces = 0;
        for(Card card : cards) {
            if(!addHidden && card.isHiddenDealer()) {
                continue;
            }

            if(card.getType() == CardType.ACE) {
                aces++;
            }
            sum += card.getType().getBlackjackValue();
        }
        while(aces != 0) {
            if(sum > 21) {
                sum -= 10;
                aces--;
            } else {
                break;
            }
        }
        return sum;
    }

}

