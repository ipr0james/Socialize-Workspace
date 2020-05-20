package net.thenova.socialize.games.roulette.bet;

import lombok.Getter;

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
public enum RouletteBetType {
    STRAIGHT(36, "Straight"),
    DOZEN(3, "Dozens (1-12, 13-24, 25-36)"),
    COLUMN(3, "Columns (1st, 2nd, 3rd)"),
    HALF(2, "Halves (1-18, 19-36)"),
    COLOR(2, "Colors (red, black)"),
    FORM(2, "Odd/Even");

    @Getter private final int multiplier;
    @Getter private final String description;

    RouletteBetType(int multiplier, String description) {
        this.multiplier = multiplier;
        this.description = description;
    }
}
