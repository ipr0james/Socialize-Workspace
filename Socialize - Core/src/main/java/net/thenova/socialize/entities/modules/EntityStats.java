package net.thenova.socialize.entities.modules;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.stat_keys.CountText;
import net.thenova.socialize.entities.modules.stat_keys.CountVoice;
import net.thenova.socialize.entities.modules.stat_keys.StatKey;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCasinoLoss;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCurrent;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsTotal;
import net.thenova.socialize.entities.modules.stat_keys.experience.PrestigeCurrent;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPCurrent;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPTotal;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPWeek;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.database.sql.SQLTransaction;

import java.sql.SQLException;
import java.util.*;
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
public final class EntityStats {

    // Set of all Stat types
    private static final Set<StatKey> KEYS = new HashSet<>();

    static {
        KEYS.addAll(Arrays.asList(new PrestigeCurrent(),
                new XPCurrent(), new XPTotal(), new XPWeek(),
                new CoinsCasinoLoss(), new CoinsCurrent(), new CoinsTotal(),
                new CountText(), new CountVoice(),
                new GangID()));
    }

    private final Entity entity;

    // Loaded stat values
    private final Map<String, Long> stats = new HashMap<>();

    /**
     * Initialise and load Entity statistics
     *
     * @param entity - Entity to be loaded
     */
    public EntityStats(final Entity entity) {
        this.entity = entity;

        // Load all data from SQL for the current user
        new SQLQuery(new DBSocialize(), "SELECT * FROM `entity_stats` WHERE `entity_id` = ?", this.entity.getID())
                .execute(res -> {
                    try {
                        while(res.next()) {
                            final String dataKey = res.getString("stat_key");
                            final StatKey key = EntityStats.KEYS.stream()
                                    .filter(stat -> stat.key().equals(dataKey))
                                    .findFirst()
                                    .orElse(null);

                            if(key != null) {
                                this.stats.put(key.key(), res.getLong("stat_value"));
                            }
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[EntityStats] - Could not load stats for entity {}", this.entity.getID(), ex);
                    }
                });

        // Collect any missing data keys from the loaded keys
        final Set<StatKey> missing = EntityStats.KEYS.stream()
                .filter(key -> !this.stats.containsKey(key.key()))
                .collect(Collectors.toSet());

        // Load any missing keys, insert the data in to SQL
        if(!missing.isEmpty()) {
            final String insert = "INSERT INTO `entity_stats` (`entity_id`, `stat_key`, `stat_value`) VALUES (?, ?, ?)";

            try {
                final SQLTransaction transaction = new SQLTransaction(new DBSocialize());
                for(StatKey key : missing) {
                  this.stats.put(key.key(), key.value());
                  transaction.query(insert, entity.getID(), key.key(), key.value());
                }

                transaction.commit();
            } catch (final SQLException ex) {
                Titan.INSTANCE.getLogger().info("[EntityStats] - Could not insert stats for entity {}", this.entity.getID(), ex);
            }
        }
    }

    /**
     * Fetch the data of a given StatKey.
     *
     * @param key - StatKey
     * @return - Return the current value for that data or the default if not found
     */
    public final Long fetch(final StatKey key) {
        return this.stats.getOrDefault(key.key(), key.value());
    }

    /**
     * Update the value of a statistic, writing the value back to statistic table.
     *
     * @param key - StatKey
     * @param value - Value to be updated
     */
    public final void update(final StatKey key, final Long value) {
        this.stats.put(key.key(), value);
        new SQLQuery(new DBSocialize(),"UPDATE `entity_stats` SET `stat_value` = ? WHERE (`entity_id`, `stat_key`) = (?, ?)", value, entity.getID(), key.key()).execute();
    }

    /**
     * Increment a value by 1.
     *
     * @param key - StatKey being incremented.
     */
    public void increment(final StatKey key) {
        this.increment(key, 1);
    }

    /**
     * Increment a value by the given amount.
     *
     * @param key - StatKey being incremented
     * @param amount - Amount to increment by
     */
    public void increment(final StatKey key, final long amount) {
        this.update(key, this.fetch(key) + amount);
    }
}
