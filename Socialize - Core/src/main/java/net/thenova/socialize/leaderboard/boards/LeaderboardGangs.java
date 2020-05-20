package net.thenova.socialize.leaderboard.boards;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.leaderboard.Leaderboard;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
public final class LeaderboardGangs extends Leaderboard {

    public LeaderboardGangs(long guildID) {
        super(guildID, GuildChannelsData.Type.LEADERBOARD_GANG, "Gangs", "coins");
    }

    @Override
    public Map<String, Long> fetch() {
        final Map<String, Long> results = new HashMap<>();

        new SQLQuery(new DBSocialize(), "SELECT `gang_gangs`.`gang_id`, `gang_data`.`data_value` FROM `gang_gangs`" +
                " INNER JOIN `gang_data` ON `gang_gangs`.`gang_id`=`gang_data`.`gang_id`" +
                " WHERE (`gang_gangs`.`guild_id`, `gang_data`.`data_key`) = (?, 'BALANCE')", super.guildID)
                .execute(res -> {
                    try {
                        Map<Long, Long> output = new HashMap<>();

                        while(res.next()) {
                            output.put(res.getLong("gang_id"), Long.valueOf(res.getString("data_value")));
                        }

                        output = output.entrySet()
                                .stream()
                                .sorted((Map.Entry.<Long, Long>comparingByValue().reversed()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                        int i = 0;
                        for(Map.Entry<Long, Long> entry : output.entrySet()) {
                            if(i >= 10) {
                                break;
                            }

                            new SQLQuery(new DBSocialize(), "SELECT `data_value` FROM `gang_data` WHERE (`gang_id`, `data_key`) = (?, 'NAME')", entry.getKey())
                                    .execute(rs -> {
                                        try {
                                            if(rs.next()) {
                                                results.put(rs.getString("data_value"), entry.getValue());
                                            }
                                        } catch (final SQLException ex) {
                                            Titan.INSTANCE.getLogger().info("[Leaderboard] [LeaderboardGangs] - Failed to retrieve data NAME for {}.", this.guildID, ex);
                                        }
                                    });

                            i++;
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[Leaderboard] [LeaderboardGangs] - Failed to retrieve data LB for {}.", this.guildID, ex);
                    }
                });

        return results;
    }
}
