package net.thenova.socialize.entities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.util.task.TaskHandler;
import net.thenova.titan.library.Titan;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public enum EntityHandler {
    INSTANCE;

    /**
     * Guild Entities - Map of all current loaded entities across all guilds
     *
     * Long - GuildID
     * Map<Long, Entity> - UserID, Entity object
     */
    private final Map<Long, Map<Long, Entity>> guildEntities = new LinkedHashMap<>();

    public void load() {
        final Logger debug = Titan.INSTANCE.getDebug();

        TaskHandler.INSTANCE.scheduleSystemRepeating(() -> {
            debug.info("[EntityHandler] - Beginning entity unload task...");
            synchronized (EntityHandler.INSTANCE) {
                this.guildEntities.forEach((guildID, entities) -> {
                    final Guild guild = Bot.getJDA().getGuildById(guildID);

                    if (guild == null) {
                        Titan.INSTANCE.getLogger().info("[EntityHandler] - Tried to unload data for '{}' but JDA returned guild as null", guildID);
                        return;
                    }

                    debug.info("[EntityHandler] - Unloading entities for '{}'", guild);

                    final Set<Long> remove = entities.values().stream()
                            .filter(entity -> {
                                if (entity.getLastFetch() + TimeUnit.MILLISECONDS.toMillis(10) > System.currentTimeMillis()) {
                                    final Member member = guild.getMemberById(entity.getUserID());

                                    return member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null;
                                } else {
                                    return false;
                                }
                            })
                            .map(Entity::getUserID)
                            .collect(Collectors.toSet());

                    remove.forEach(entityID -> {
                        entities.remove(entityID);
                        debug.info("[Entity] - '{}' has been unloaded successfully", entityID);
                    });

                    debug.info("[EntityHandler] - Entity unload completed for for '{}'", guild);
                });
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Check if a member is currently loaded in to the cache.
     *
     * @param member - Member being checked
     * @return - Return whether the user was found in the cache.
     */
    public boolean isLoaded(final Member member) {
        return this.isLoaded(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public boolean isLoaded(final long guildID, final long userID) {
        return this.guildEntities.containsKey(guildID)
                && this.guildEntities.get(guildID).containsKey(userID);
    }

    /**
     * Quick access method to retrieve entity
     *
     * @param member - Member to be fetched as Entity
     * @return - Return the Entity object
     */
    public Entity getEntity(final Member member) {
        synchronized (EntityHandler.INSTANCE) {
            // Means user is no longer in the guild. Kicked/Banned/Left while being fetched.
            if (member == null) {
                return null;
            }

            final Guild guild = member.getGuild();
            final long guildID = guild.getIdLong();
            final long userID = member.getUser().getIdLong();

            if (!this.guildEntities.containsKey(guildID)) {
                Map<Long, Entity> entities = new LinkedHashMap<>();
                Entity entity = new Entity(member);
                entities.put(userID, entity);
                this.guildEntities.put(guildID, entities);
                return entity;
            }

            final Map<Long, Entity> entities = this.guildEntities.get(guildID);
            Entity entity = entities.get(userID);

            if (entity == null) {
                entity = new Entity( member);
                entities.put(userID, entity);
            } else {
                entity.setLastFetch(System.currentTimeMillis());
            }

            return entity;
        }
    }

    /**
     * Fetch a member from the cache or load/create them depending on whether they're present in the database
     * @param guild - Current guild as data is per guild
     * @param userID - UserID used as unique ID for storage
     * @return - Return the Entity object for the user
     */
    public Entity getEntity(final Guild guild, final long userID) {
        return getEntity(guild.getMemberById(userID));
    }

    /**
     * @return - Return a list of all current loaded entities.
     */
    public List<Entity> getEntities() {
        synchronized (EntityHandler.INSTANCE) {
            final List<Entity> rtn = new ArrayList<>();
            this.guildEntities.values().forEach(val -> rtn.addAll(val.values()));
            return rtn;
        }
    }

    /**
     * Collect all Entity objects loaded for a specific guild.
     *
     * @param guildID - GuildID
     * @return - List<Entity> of all Entities for a specified guild
     */
    public List<Entity> getEntities(final long guildID) {
        synchronized (EntityHandler.INSTANCE) {
            return new ArrayList<>(this.guildEntities.get(guildID).values());
        }
    }
}
