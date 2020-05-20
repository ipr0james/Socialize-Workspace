package net.thenova.socialize.games.lottery;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.Entity;
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
public final class LotteryData {

    private final Map<Long, Map<Long, Long>> guildLotteries = new HashMap<>();

    public LotteryData() {
        new SQLQuery(new DBSocialize(), "SELECT * FROM `games_lottery`")
                .execute(res -> {
                    try {
                        while(res.next()) {
                            long guildID = res.getLong("guild_id");
                            if(!this.guildLotteries.containsKey(guildID)) {
                                this.guildLotteries.put(guildID, new HashMap<>());
                            }

                            this.guildLotteries.get(guildID).put(res.getLong("user_id"), res.getLong("amount"));
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[LotteryData] - Failed to select all exisiting lottery entries", ex);
                    }
                });
    }

    public final Map<Long, Long> getTickets(final long guildID) {
        return this.guildLotteries.getOrDefault(guildID, new HashMap<>());
    }

    public synchronized void addTicket(final Entity entity, long count) {
        final long guild = entity.getGuildID();
        if(!this.guildLotteries.containsKey(guild)) {
            this.guildLotteries.put(guild, new HashMap<>());
        }

        final long user = entity.getUserID();
        final Map<Long, Long> tickets = this.guildLotteries.get(guild);
        if(tickets.containsKey(user)) {
            count += tickets.get(user);
            new SQLQuery(new DBSocialize(),"UPDATE `games_lottery` SET `amount` = ? WHERE (`guild_id`, `user_id`) = (?, ?)", count, guild, user).execute();
        } else {
            new SQLQuery(new DBSocialize(), "INSERT `games_lottery` (`guild_id`, `user_id`, `amount`) VALUES (?, ?, ?)", guild, user, count).execute();
        }

        tickets.put(user, count);
    }

    public void resetGuild(final long guildID) {
        this.guildLotteries.remove(guildID);

        new SQLQuery(new DBSocialize(),"DELETE FROM `games_lottery` WHERE `guild_id` = ?", guildID).execute();
    }
}
