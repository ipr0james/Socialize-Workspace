package net.thenova.socialize.level;

import lombok.AllArgsConstructor;
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
@Getter
@AllArgsConstructor
public final class Level {

    private final Integer level;
    private final Long requirement;

    /**
     * Quick access method to fetch the members next level.
     *
     * @return - Return the next Level object, or null
     */
    public Level getNextLevel() {
        try {
            return LevelManager.INSTANCE.getLevel(level + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Quick access method to fetch the members previous level.
     *
     * @return - Return the next Level object, or null
     */
    public Level getPreviousLevel() {
        try {
            return LevelManager.INSTANCE.getLevel(this.level -1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
