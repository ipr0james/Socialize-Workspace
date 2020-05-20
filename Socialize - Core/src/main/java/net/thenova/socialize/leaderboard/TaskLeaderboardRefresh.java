package net.thenova.socialize.leaderboard;

import net.thenova.socialize.leaderboard.boards.LeaderboardCoins;
import net.thenova.socialize.leaderboard.boards.LeaderboardGangs;
import net.thenova.socialize.leaderboard.boards.LeaderboardXPWeekly;

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
public final class TaskLeaderboardRefresh implements Runnable {

    private Set<Leaderboard> boards = new HashSet<>();

    public TaskLeaderboardRefresh(final long guildID) {
        this.boards.add(new LeaderboardCoins(guildID));
        this.boards.add(new LeaderboardGangs(guildID));
        this.boards.add(new LeaderboardXPWeekly(guildID));
    }

    @Override
    public void run() {
        this.boards.forEach(Leaderboard::refresh);
    }
}
