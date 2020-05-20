package net.thenova.socialize.gangs.gang.upgrades.upgrades;

import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgrade;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;

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
public final class GangUpgradeBank extends GangUpgrade {

    private final List<Long> balances = Arrays.asList(1000L, 5000L, 10000L, 25000L, 50000L, 75000L, 120000L, 200000L, 350000L, 600000L, 1000000L);

    public GangUpgradeBank() {
        super(GangUpgradeType.BANK, "Bank Limit Upgrade",
                new String[] {
                    "Increase the maximum capacity that your",
                    "gang bank can hold"
                });
    }

    @Override
    public final String[] description(final int level) {
        return new String[] {
                "Current Maximum: " + this.getMaxBalance(level - 1),
                "New Maximum: " + this.getMaxBalance(level)
        };
    }

    @Override
    public long[] costs() {
        return new long[] {
                500,
                1000,
                2000,
                4000,
                8000,
                16000,
                32000,
                64000,
                128000,
                248000
        };
    }

    @Override
    public void handle(Gang gang, int level) { }

    /**
     * Check the Maximum Bank balance based around the Upgrade level
     * @param level - Level to be checked
     * @return - Return the max balance
     */
    private long getMaxBalance(int level) {
        return this.balances.get(level);
    }

    public long getMaxBalance() {
        return this.getMaxBalance(this.level);
    }
}
