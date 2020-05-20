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
public enum CardSuit {

    /**
     * A red heart card.
     */
    HEARTS("Hearts"),

    /**
     * A black heart card.
     */
    SPADES("Spades"),

    /**
     * A clover.
     */
    CLUBS("Clubs"),

    /**
     * A diamond.
     */
    DIAMONDS("Diamonds");

    private final String displayName;

    /**
     * Creates a new card suit.
     * @param displayName The display name.
     */
    CardSuit(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name.
     * @return The name
     */
    public String getDisplayName() {
        return displayName;
    }

}
