package net.thenova.socialize.gangs.gang.upgrades;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

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
@RequiredArgsConstructor
public abstract class GangUpgrade {

    // Global
    private final GangUpgradeType type;
    private final String name;
    private final String[] description;

    // Per Gang
    protected int level = 0;

    public abstract String[] description(final int level);
    public abstract long[] costs();
    protected abstract void handle(final Gang gang, final int level);

    public final void load(final Gang gang, final int level) {
        this.level = level;

        this.handle(gang, level);
    }

    public final long cost(final int level) {
        try {
            return costs()[level - 1];
        } catch (final IndexOutOfBoundsException ex) {
            Titan.INSTANCE.getLogger().info("[GangUpgrade] - cost for level {} threw IndexOutOfBounds for costs() {}", level, costs().length);
            return Long.MAX_VALUE;
        }
    }

    public final boolean isMaxLevel() {
        return this.level >= this.costs().length;
    }
    public final int getMaxLevel() {
        return this.costs().length;
    }

    public final void upgrade(final Gang gang, final int level) {
        this.level = level;
        final long cost;
        if((cost = this.cost(level)) == Long.MAX_VALUE) {
            Titan.INSTANCE.getLogger().info("[GangUpgrade] - Failed to retrieve cost for upgrade() level {}", level);
            return;
        }

        gang.getBank().take(cost);

        new SQLQuery(new DBSocialize(), "UPDATE `gang_upgrades` SET `upgrade_level` = ? WHERE (`gang_id`, `upgrade_type`) = (?, ?)",
                    level, gang.getID(), this.type.toString())
                .execute();

        this.handle(gang, level);
    }
    /*private final GangUpgradeType type;
    private final String name;
    private final String[] description;

    protected int level;

    public GangUpgrade(GangUpgradeType type, String name, String[] description) {
        this.type = type;
        this.name = name;
        this.description = description;

        this.level = 0;
    }

    public abstract String[] getLevelDescription(int level);
    public abstract List<Integer> prices();

    public final void load(final Gang gang, final int level) {
        this.level = level;
        this.handle(gang, level);
    }

    public final void upgrade(final Gang gang, final int level) {
        this.level = level;
        gang.getBank().take(this.getLevelCost(level - 1));

        new SQLQuery(new DBSocialize(), "UPDATE `gang_upgrades` SET `upgrade_level` = ? WHERE (`gang_id`, `upgrade_type`) = (?, ?)",
                level, gang.getID(), this.type.toString())
                .execute();

        this.handle(gang, level);
    }
    protected abstract void handle(final Gang gang, final int level);

    public final String getName() {
        return this.name;
    }
    public final String[] getDescription() {
        return this.description;
    }

    public final int getLevel() {
        return this.level;
    }
    public final int getMaxLevel() {
        return this.prices().size();
    }
    public final boolean isMaxLevel() {
        return this.prices().size() <= this.level;
    }

    public final Integer getLevelCost(final int level) {
        return this.isMaxLevel() ? null : this.prices().get(level - 1);
    }*/
}
