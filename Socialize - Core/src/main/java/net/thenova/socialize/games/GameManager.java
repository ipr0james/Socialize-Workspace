package net.thenova.socialize.games;

import net.thenova.socialize.games.lottery.LotteryData;

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
public enum GameManager {
    INSTANCE;

    private LotteryData lotteryData;
    /**
     * //TODO:
     * - Chat Games
     * - Redo Casino Games into a Game<> abstract
     */

    public void load() {
        this.lotteryData = new LotteryData();
    }

    public final LotteryData getLotteryData() {
        return this.lotteryData;
    }
}
