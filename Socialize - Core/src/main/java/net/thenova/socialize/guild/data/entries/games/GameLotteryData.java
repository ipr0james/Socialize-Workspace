package net.thenova.socialize.guild.data.entries.games;

import net.thenova.socialize.guild.data.types.TypeMap;
import net.thenova.socialize.guild.data.types.property.Property;

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
public final class GameLotteryData extends TypeMap {

    public enum Type {
        TICKET_MAX("ticket_max", 50L),
        TICKET_PRICE("ticket_price", 10L);

        private final String name;
        private final Long def;

        Type(final String name, final Long def) {
            this.name = name;
            this.def = def;
        }

        @Override
        public String toString() {
            return this.name;
        }
        public long def() {
            return this.def;
        }
    }

    @Override
    protected String getUniqueIdentifier() {
        return "gang_data";
    }

    /**
     * Return the Property using Type, default value automatically assigned
     *
     * @param type - Type being selected
     * @return - Return Property of Type or default value
     */
    public Property get(final Type type) {
        return this.get(type.toString()).defaulting(type.def);
    }
}

