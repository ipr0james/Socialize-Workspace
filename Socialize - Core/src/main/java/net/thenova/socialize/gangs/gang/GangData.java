package net.thenova.socialize.gangs.gang;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.database.sql.SQLTransaction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
public final class GangData {

    private final Gang gang;
    private final Map<Type, Object> data = new HashMap<>();

    public enum Type {
        NAME(String.class, "temp"),
        OPEN_STATUS(Boolean.class, false),
        BALANCE(Long.class, 0),
        OVERDRAWN(Integer.class, 0),
        MULTIPLIER_EXPERIENCE(Double.class, 0.0),
        MULTIPLIER_COINS(Double.class, 0.0);

        private final Class<?> type;
        private final Object def;

        Type(final Class<?> type, final Object def) {
            this.type = type;
            this.def = def;
        }
    }

    /**
     * Initialise Gang data.
     *
     * @param gang - Gang being loaded
     */
    GangData(final Gang gang) {
        this.gang = gang;

        new SQLQuery(new DBSocialize(), "SELECT * FROM `gang_data` WHERE `gang_id` = ?", this.gang.getID())
                .execute(res -> {
                    try {
                        while(res.next()) {
                            try {
                                this.data.put(Type.valueOf(res.getString("data_key")), res.getObject("data_value"));
                            } catch (IllegalArgumentException ignored) { }
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[GangData] - GangData has failed initialization.", ex);
                    }
                });

        final Set<Type> missing = Arrays.stream(Type.values())
                .filter(type -> !this.data.containsKey(type))
                .collect(Collectors.toSet());

        if(!missing.isEmpty()) {
            final String insert = "INSERT INTO `gang_data` (`gang_id`, `data_key`, `data_value`) VALUES (?, ?, ?)";

            try {
                final SQLTransaction transaction = new SQLTransaction(new DBSocialize());
                for(Type key : missing) {
                    this.data.put(key, key.def);
                    transaction.query(insert, gang.getID(), key.toString(), key.def.toString());
                }

                transaction.commit();
            } catch (final SQLException ex) {
                Titan.INSTANCE.getLogger().info("[GangData] - Could not insert data for gang {}", this.gang.getID(), ex);
            }
        }
    }

    /**
     * Fetch a data entry from the collection, automatically cast.
     *
     * @param type - Type
     * @return - Return Object of data
     */
    public final Object fetch(Type type) {
        final Object obj = this.data.getOrDefault(type, type.def);

        if (Long.class.equals(type.type)) {
            return Long.parseLong(obj.toString());
        }

        if (Boolean.class.equals(type.type)) {
            return Boolean.parseBoolean(obj.toString());
        }

        if (Integer.class.equals(type.type)) {
            return Integer.parseInt(obj.toString());
        }

        if (Double.class.equals(type.type)) {
            return Double.parseDouble(obj.toString());
        }

        return this.data.getOrDefault(type, type.def);
    }

    /**
     * Update a data key back to database and local cache.
     *
     * @param type - Type
     * @param value - Value being set
     */
    public final void set(Type type, Object value) {
        this.data.put(type, value);
        new SQLQuery(new DBSocialize(), "UPDATE `gang_data` SET `data_value` = ? WHERE (`gang_id`, `data_key`) = (?, ?)",
                    value.toString(), this.gang.getID(), type.toString())
                .execute();
    }
}
