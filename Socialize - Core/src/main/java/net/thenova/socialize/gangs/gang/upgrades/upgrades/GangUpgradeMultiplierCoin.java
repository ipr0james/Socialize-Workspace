package net.thenova.socialize.gangs.gang.upgrades.upgrades;

import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
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
public final class GangUpgradeMultiplierCoin extends GangUpgrade {

    private static final double MULTIPLIER_DEFAULT = 0;
    private static final double MULTIPLIER_INCREMENT = 0.1;

    public GangUpgradeMultiplierCoin() {
        super(GangUpgradeType.MULTIPLIER_COIN, "Coin Multiplier Upgrade",
                new String[] {
                    "Increase the amount of coins",
                    "gained by all members of the gang."
                });
    }

    @Override
    public final String[] description(final int level) {
        return new String[] {
                "Current Multiplier: `" + (1.0 + this.getMultiplier(level - 1)) + "`",
                "New Multiplier: `" + (1.0 + this.getMultiplier(level)) + "`"
        };
    }

    @Override
    public final long[] costs() {
        return new long[] {
                1000,
                4500,
                9000,
                20000,
                35000,
                60000,
                105000,
                300000,
                600000,
                900000
        };
    }

    @Override
    public final void handle(final Gang gang, final int level) {
        gang.getData().set(GangData.Type.MULTIPLIER_COINS, this.getMultiplier(level));

        gang.getMembers().getMembers().stream()
                .filter(member -> EntityHandler.INSTANCE.isLoaded(gang.getGuildID(), member.getUserID()))
                .forEach(member -> {
                    final Entity entity = member.getEntity();
                    assert entity != null;
                    entity.getMultiplier().check();
                });
    }

    /**
     * Check the Entity xp/coin multiplier against the upgrade level
     * @param level - Level to be checked
     * @return - Return the correct multiplier for entities in the gang.
     */
    public final double getMultiplier(final int level) {
        return level * MULTIPLIER_INCREMENT;
    }

    public final double getMultiplier() {
        return this.getMultiplier(this.level);
    }
}
