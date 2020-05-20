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
public final class GangUpgradeMembers extends GangUpgrade {

    public static final int MEMBERS_DEFAULT = 5;
    private static final int MEMBERS_INCREASE = 5;

    public GangUpgradeMembers() {
        super(GangUpgradeType.MEMBERS, "Max Members Upgrade",
                new String[] {
                    "Increase the maximum amount of",
                    "members that can be in your gang."
                });
    }

    @Override
    public final String[] description(final int level) {
        return new String[] {
                "Max Members: `" + this.getMaxMembers(level - 1) + "`",
                "New Max Members: `" + this.getMaxMembers(level) + "`"
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
                120000,
                200000,
                400000
        };
    }

    @Override
    public final void handle(final Gang gang, final int level) { }

    /**
     * Check the Entity xp/coin multiplier against the upgrade level
     * @param level - Level to be checked
     * @return - Return the correct multiplier for entities in the gang.
     */
    public final int getMaxMembers(final int level) {
        return (level * MEMBERS_INCREASE) + MEMBERS_DEFAULT;
    }

    public final int getMaxMembers() {
        return this.getMaxMembers(super.level);
    }
}
