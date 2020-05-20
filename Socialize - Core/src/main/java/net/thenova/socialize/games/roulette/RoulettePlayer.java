package net.thenova.socialize.games.roulette;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCasinoLoss;
import net.thenova.socialize.games.roulette.bet.RouletteBet;
import net.thenova.socialize.games.roulette.bet.RouletteBetType;

import java.util.Arrays;
import java.util.List;

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
@Getter
public final class RoulettePlayer {

    private final Entity entity;
    private final Member member;
    private final RouletteBet bet;

    @Getter
    private long winnings;

    public RoulettePlayer(Entity entity, Member member, RouletteBet bet) {
        this.entity = entity;
        this.member = member;
        this.bet = bet;
    }

    public boolean play(int number) {
        final RouletteBetType type = this.bet.getType();
        final String value = this.bet.getValue();
        final long bet = this.bet.getBet();

        boolean won = false;

        switch(type) {
            case COLOR:
                if(value.equals("red")) {
                   final List<Integer> reds = Arrays.asList(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

                   if(reds.contains(number)) {
                       won = true;
                   }
                } else {
                    final List<Integer> blues = Arrays.asList(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35);

                    if(blues.contains(number)) {
                        won = true;
                    }
                }
                break;
            case FORM:
                if(value.equals("even")) {
                    if(number % 2 == 0) {
                        won = true;
                    }
                } else {
                    if(number % 2 != 0) {
                        won = true;
                    }
                }
                break;
            case HALF:
                if(value.equals("1-18")) {
                    if(number > 0 && number <= 18) {
                        won = true;
                    }
                } else {
                    if(number > 18 && number <= 36) {
                        won = true;
                    }
                }
                break;
            case COLUMN:
                switch(value) {
                    case "1st":
                        if(Arrays.asList(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34).contains(number)) {
                            won = true;
                        }
                        break;
                    case "2nd":
                        if(Arrays.asList(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35).contains(number)) {
                            won = true;
                        }
                        break;
                    case "3rd":
                        if(Arrays.asList(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36).contains(number)) {
                            won = true;
                        }
                        break;
                }
                break;
            case DOZEN:
                switch (value) {
                    case "1-12":
                        if(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).contains(number)) {
                            won = true;
                        }
                        break;
                    case "13-24":
                        if(Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24).contains(number)) {
                            won = true;
                        }
                        break;
                    case "25-36":
                        if(Arrays.asList(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36).contains(number)) {
                            won = true;
                        }
                        break;
                }
                break;
            case STRAIGHT:
                if(number == Integer.parseInt(value)) {
                    won = true;
                }
        }

        if(won) {
            long winnings = bet * type.getMultiplier();

            this.entity.getBalance().add(winnings, EntityBalance.Reason.CASINO);
            this.winnings = winnings;
            return true;
        } else {
            this.entity.getStats().increment(new CoinsCasinoLoss(), bet);
            this.winnings = -1;
            return false;
        }
    }
}
