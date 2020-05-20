package net.thenova.socialize.games.card;

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
public enum CardType {

    /**
     * 2
     */
    TWO("Two", 2, 2, "2"),

    /**
     * 3
     */
    THREE("Three", 3, 3, "3"),

    /**
     * 4
     */
    FOUR("Four", 4, 4, "4"),

    /**
     * 5
     */
    FIVE("Five", 5, 5, "5"),

    /**
     * 6
     */
    SIX("Six", 6, 6, "6"),

    /**
     * 7
     */
    SEVEN("Seven", 7, 7, "7"),

    /**
     * 8
     */
    EIGHT("Eight", 8, 8, "8"),

    /**
     * 9
     */
    NINE("Nine", 9, 9, "9"),

    /**
     * 10
     */
    TEN("Ten", 10, 10, "10"),

    /**
     * Jack
     */
    JACK("Jack", 11, 10, "J"),

    /**
     * Queen
     */
    QUEEN("Queen", 12, 10, "Q"),

    /**
     * King
     */
    KING("King", 13, 10, "K"),

    /**
     * Ace
     */
    ACE("Ace", 14, 11, "A");

    private final String displayName;

    private final int value;
    private final int blackjackValue;

    private final String identifier;

    /**
     * Creates a new card type.
     * @param displayName - The display name.
     * @param blackjackValue - The value of the type, higher is better.
     */
    CardType(String displayName, int value, int blackjackValue, String identifier) {
        this.displayName = displayName;

        this.value = value;
        this.blackjackValue = blackjackValue;

        this.identifier = identifier;
    }

    /**
     * Gets the display name.
     * @return - The name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Gets the Normal value of a card.
     * @return - The normal value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Gets the Blackjack value of the card.
     * @return - The value.
     */
    public int getBlackjackValue() {
        return this.blackjackValue;
    }

    /**
     * Gets the card identifier.
     * @return - The identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }

}
