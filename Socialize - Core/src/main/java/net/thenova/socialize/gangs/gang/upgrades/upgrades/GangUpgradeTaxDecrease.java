package net.thenova.socialize.gangs.gang.upgrades.upgrades;

import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgrade;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;

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
public final class GangUpgradeTaxDecrease extends GangUpgrade {

    private static final long DEFAULT = 300;

    public GangUpgradeTaxDecrease() {
        super(GangUpgradeType.TAX_DECREASE, "Tax Decrease Upgrade",
                new String[] {
                        "Decrease your daily tax amount"
                });
    }


    @Override
    public final String[] description(final int level) {
        return new String[] {
                "Tax Decrease : " + this.getTaxDecrease(level - 1),
                "New Tax Decrease: " + this.getTaxDecrease(level)
        };
    }

    @Override
    public final long[] costs() {
        return new long[] {
                1000,
                3000,
                6000,
                12000,
                22000,
                40000,
                67000,
                150000,
                400000,
                800000
        };
    }

    @Override
    public final void handle(final Gang gang, final int level) { }

    /**
     * Return the Tax Decrease for the gang
     * @param level - Level to be checked
     * @return - Return the max balance
     **/
    public final long getTaxDecrease(final int level) {
        return (DEFAULT - (20 * level));
    }

    public final long getTax() {
        return this.getTaxDecrease(this.level);
    }
}
