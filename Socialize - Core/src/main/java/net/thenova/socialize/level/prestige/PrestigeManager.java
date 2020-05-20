package net.thenova.socialize.level.prestige;

import de.arraying.kotys.JSONArray;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Bot;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.level.prestige.LevelPrestigeData;
import net.thenova.socialize.guild.data.entries.level.prestige.LevelPrestigeRolesData;
import net.thenova.titan.library.Titan;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2020 ipr0james
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
public enum PrestigeManager {
    INSTANCE;

    public void load() {

    }

    public long getMaxPrestige(final long guildID) {
        return GuildHandler.INSTANCE.fetch(guildID, new LevelPrestigeData()).get(LevelPrestigeData.Type.MAX).asLong();
    }

    public Set<Role> getPrestigeRoles(final long guildID, final long prestige) {
        final LevelPrestigeRolesData data = GuildHandler.INSTANCE.fetch(guildID, new LevelPrestigeRolesData());
        final Set<Role> roles = new HashSet<>();

        if(data.contains(String.valueOf(prestige))) {
            final Guild guild = Bot.getJDA().getGuildById(guildID);
            assert guild != null;

            final JSONArray array = data.get(String.valueOf(prestige)).asArray();

            for(int i = 0; i < array.length(); i++) {
                final Role role = guild.getRoleById(array.string(i));

                if(role != null) {
                    roles.add(role);
                } else {
                    final String val = array.string(i);
                    array.delete(i);
                    data.save();

                    Titan.INSTANCE.getDebug().info("[PrestigeManager] - {} has been removed from roles for level {} in guild {} as it was invalid",
                            val, prestige, guildID);
                    //TODO - Test
                }
            }
        }

        return roles;
    }

    public Set<Role> getAllPrestigeRoles(final long guildID, final long prestige) {
        final LevelPrestigeRolesData data = GuildHandler.INSTANCE.fetch(guildID, new LevelPrestigeRolesData());
        final Set<Role> roles = new HashSet<>();
        final Guild guild = Bot.getJDA().getGuildById(guildID);
        assert guild != null;

        for(long i = prestige; i > 0; i--) {
            if(data.contains(String.valueOf(prestige))) {
                final JSONArray array = data.get(String.valueOf(prestige)).asArray();

                for(int j = 0; j < array.length(); j++) {
                    final Role role = guild.getRoleById(array.string(j));

                    if(role != null) {
                        roles.add(role);
                    } else {
                        final String val = array.string(j);
                        array.delete(j);
                        data.save();

                        Titan.INSTANCE.getDebug().info("[PrestigeManager] - {} has been removed from roles for level {} in guild {} as it was invalid",
                                val, prestige, guildID);
                        //TODO - Test
                    }
                }
            }
        }

        return roles;
    }
}
