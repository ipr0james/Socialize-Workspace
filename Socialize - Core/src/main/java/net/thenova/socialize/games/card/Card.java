package net.thenova.socialize.games.card;

import net.thenova.socialize.Bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2018 Arraying
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
public final class Card {

    private final CardSuit suit;
    private final CardType type;
    private boolean hiddenDealer = false;

    private final long emoteID;

    /**
     * Creates a new card.
     * @param suit The card suit.
     * @param type The card type.
     */
    public Card(CardSuit suit, CardType type) {
        this.suit = suit;
        this.type = type;

        this.emoteID = Bot.getJDA()
                .getEmotesByName(type.getIdentifier() + "_" + suit.getDisplayName(), false)
                .get(0)
                .getIdLong();
    }

    /**
     * Generates all cards, so a full set.
     * @return A list of cards.
     */
    public static List<Card> deck() {
        List<Card> cards = new ArrayList<>();
        for(CardSuit suit : CardSuit.values()) {
            for(CardType type : CardType.values()) {
                cards.add(new Card(suit, type));
            }
        }
        Collections.shuffle(cards);
        return cards;
    }

    /**
     * Sets the card's hidden dealer value.
     * @param hiddenDealer True if it is hidden, false otherwise.
     */
    public void setHiddenDealer(boolean hiddenDealer) {
        this.hiddenDealer = hiddenDealer;
    }

    /**
     * Gets the type.
     * @return The type.
     */
    public CardType getType() {
        return type;
    }

    public boolean isHiddenDealer() {
        return this.hiddenDealer;
    }

    public String getEmoji() {
        return "<:" + type.getIdentifier() + "_" + suit.getDisplayName() + ":" + this.emoteID + ">";
    }
}
