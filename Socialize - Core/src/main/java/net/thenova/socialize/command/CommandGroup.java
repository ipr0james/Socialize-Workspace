package net.thenova.socialize.command;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

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
@SuppressWarnings("WeakerAccess")
@Getter
public final class CommandGroup {

    private final Set<String> commands = new HashSet<>();
    private final Set<Long> channels = new HashSet<>();

    public CommandGroup(final String name, final JSON json) {
        final JSONArray channels = json.array("whitelist");
        for(int i = 0; i < channels.length(); i++) {
            this.channels.add(channels.large(i));
        }

        final JSONArray commands = json.array("commands");
        for(int i = 0; i < commands.length(); i++) {
            this.commands.add(commands.string(i));
        }
    }
}
