package net.thenova.socialize.gangs.gang.upgrades;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.*;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.database.sql.SQLTransaction;

import java.sql.SQLException;
import java.util.*;

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
public final class GangUpgrades {

    private final Map<GangUpgradeType, GangUpgrade> upgrades = new LinkedHashMap<>();

    /**
     * Load in a Gangs Upgrades
     * @param gang - Gang being loaded
     */
    public GangUpgrades(Gang gang) {

        this.upgrades.put(GangUpgradeType.BANK, new GangUpgradeBank());
        this.upgrades.put(GangUpgradeType.MEMBERS, new GangUpgradeMembers());
        this.upgrades.put(GangUpgradeType.MULTIPLIER_COIN, new GangUpgradeMultiplierCoin());
        this.upgrades.put(GangUpgradeType.MULTIPLIER_XP, new GangUpgradeMultiplierXP());
        this.upgrades.put(GangUpgradeType.TAX_DECREASE, new GangUpgradeTaxDecrease());

        final List<GangUpgradeType> keys = new ArrayList<>(Arrays.asList(GangUpgradeType.values()));
        new SQLQuery(new DBSocialize(), "SELECT `upgrade_type`, `upgrade_level` FROM `gang_upgrades` WHERE `gang_id` = ?", gang.getID())
                .execute(res -> {
                    try {
                        while (res.next()) {
                            final GangUpgradeType type = GangUpgradeType.valueOf(res.getString("upgrade_type"));
                            final GangUpgrade upgrade = this.upgrades.get(type);
                            final int level = res.getInt("upgrade_level");

                            upgrade.load(gang, level);

                            keys.remove(type);
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[GangUpgrades] - Failed to load Gang Upgrade data", ex);
                    }
                }
        );

        try {
            if (!keys.isEmpty()) {
                final String insert = "INSERT INTO `gang_upgrades` (`gang_id`, `upgrade_type`, `upgrade_level`) VALUES (?, ?, ?)";
                final SQLTransaction transaction = new SQLTransaction(new DBSocialize());

                for (GangUpgradeType key : keys) {
                    final GangUpgrade upgrade = this.upgrades.get(key);
                    final int level = upgrade.getLevel();

                    transaction.query(insert, gang.getID(), key.toString(), level);
                    upgrade.load(gang, level);
                }

                transaction.commit();
            }
        } catch (final SQLException ex) {
            Titan.INSTANCE.getLogger().info("[GangUpgrades] - Failed to insert new Gang keys", ex);
        }
    }

    /**
     * Return the Gang Upgrade object for the given rank-up
     * @param type - Gang upgrade type
     * @return - Return the instance of Abstract GangUpgrade
     */
    public GangUpgrade fetch(GangUpgradeType type) {
        return this.upgrades.get(type);
    }

    /**
     * Return a list of all current upgrades.
     *
     * @return - List of upgrades
     */
    public List<GangUpgrade> getUpgrades() {
        return new ArrayList<>(this.upgrades.values());
    }
}