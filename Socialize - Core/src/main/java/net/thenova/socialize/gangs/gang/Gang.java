package net.thenova.socialize.gangs.gang;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.gang.member.GangMembers;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgrades;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.database.sql.SQLTransaction;

import java.sql.SQLException;

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
public final class Gang {

    private final long id;
    private final long guildID;

    private final GangData data;
    private final GangBank bank;
    private final GangMembers members;
    private final GangUpgrades upgrades;

    public Gang(final long id, final long guildID) {
        this.id = id;
        this.guildID = guildID;

        this.data = new GangData(this);

        this.bank = new GangBank(this);
        this.members = new GangMembers(this);
        this.upgrades = new GangUpgrades(this);
    }

    public final void disband() {
        try {
            new SQLTransaction(new DBSocialize()).query("DELETE FROM `gang_members` WHERE `gang_id` = ?", this.id)
                    .query("DELETE FROM `gang_upgrades` WHERE `gang_id` = ?", this.id)
                    .query("DELETE FROM `gang_data` WHERE `gang_id` = ?", this.id)
                    .query("DELETE FROM `gang_gangs` WHERE `gang_id` = ?", this.id)
                    .commit();
        } catch (final SQLException ex) {
            Titan.INSTANCE.getLogger().info("[Gang] - Failed with deletion of gang.", ex);
        }

        this.members.getMembers().forEach(member -> {
            if(member.getEntity() != null) {
                member.getEntity().getStats().update(new GangID(), -1L);
                //TODO : member.removeNickname();
            } else {
                new SQLQuery(new DBSocialize(), "UPDATE `entity_data` SET `data_value` = ? WHERE (`guild_id`, `user_id`, `data_key`) = (?, ?, 'gang_id')",
                        null, this.guildID, member.getUserID())
                        .execute();
            }
        });
    }

    public final long getID() {
        return this.id;
    }
    public final long getGuildID() {
        return this.guildID;
    }

    public final GangData getData() {
        return this.data;
    }
    public final GangBank getBank() {
        return this.bank;
    }
    public final GangMembers getMembers() {
        return this.members;
    }
    public final GangUpgrades getUpgrades() {
        return this.upgrades;
    }
}
