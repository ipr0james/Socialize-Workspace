package net.thenova.socialize.leaderboard.boards;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.leaderboard.Leaderboard;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
public final class LeaderboardCoins extends Leaderboard {

    public LeaderboardCoins(final long guildID) {
        super(guildID, GuildChannelsData.Type.LEADERBOARD_COINS, "Coins", "coins");
    }

    @Override
    public Map<String, Long> fetch() {
        final Map<String, Long> results = new HashMap<>();

        new SQLQuery(new DBSocialize() , "SELECT `entity_data`.`user_id`, `entity_stats`.`stat_value` FROM `entity_data`"
                + " INNER JOIN `entity_stats` ON `entity_data`.`entity_id`=`entity_stats`.`entity_id`"
                + " WHERE (`entity_data`.`guild_id`, `entity_stats`.`stat_key`) = (?, 'coins')"
                + " ORDER BY `entity_stats`.`stat_value` DESC LIMIT 10", super.guildID)
                .execute(res -> {
                    try {
                        while (res.next()) {
                            results.put("<@" + res.getString("user_id") + ">", res.getLong("stat_value"));
                        }
                    } catch (SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[Leaderboard] [LeaderboardCoins] - Failed to retrieve data for {}.", this.guildID, ex);
                    }
                });

        return results;
    }
}
