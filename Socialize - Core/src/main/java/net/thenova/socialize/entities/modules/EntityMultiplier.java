package net.thenova.socialize.entities.modules;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Bot;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildDonorData;
import net.thenova.socialize.guild.data.entries.GuildRoleData;
import net.thenova.titan.library.Titan;

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
public final class EntityMultiplier {

    public enum Type {
        EXPERIENCE,
        COINS
    }

    private final Entity entity;

    private final Map<Type, Double> multipliers = new HashMap<>();

    public EntityMultiplier(final Entity entity) {
        this.entity = entity;

        this.check();
    }

    public final void check() {
        final Guild guild = Bot.getJDA().getGuildById(this.entity.getGuildID());
        final Member member;
        if(guild == null || (member = guild.getMemberById(this.entity.getUserID())) == null) {
            return;
        }

        this.multipliers.clear();

        final Role role = GuildHandler.INSTANCE.fetch(this.entity.getGuildID(), new GuildRoleData()).getRole(GuildRoleData.Type.DONOR);
        if(role != null && member.getRoles().stream().anyMatch(r -> r.getIdLong() == role.getIdLong())) {
            final GuildDonorData data = GuildHandler.INSTANCE.fetch(this.entity.getGuildID(), new GuildDonorData());

            this.multipliers.put(Type.COINS, 1.0 + data.fetch(GuildDonorData.Type.DONOR_MULTIPLIER_COINS));
            this.multipliers.put(Type.EXPERIENCE, 1.0 + data.fetch(GuildDonorData.Type.DONOR_MULTIPLIER_EXPERIENCE));
        }

        final long gangID = this.entity.getStats().fetch(new GangID());
        if(gangID != -1) {
            final Gang gang = GangManager.INSTANCE.getGang(this.entity.getGuildID(), gangID);
            if(gang == null) {
                Titan.INSTANCE.getLogger().info("[EntityMultiplier] - Gang did not exist, but entity had gang ID, GangID: {}, UserID: {}", gangID, entity.getUserID());
                return;
            }

            final GangData data = gang.getData();
            final double coin;
            if((coin = (double) data.fetch(GangData.Type.MULTIPLIER_COINS)) > 0.0) {
                this.multipliers.put(Type.COINS, this.fetch(Type.COINS) + coin);
            }

            final double xp;
            if((xp = (double) data.fetch(GangData.Type.MULTIPLIER_EXPERIENCE)) > 0.0) {
                this.multipliers.put(Type.COINS, this.fetch(Type.EXPERIENCE) + xp);
            }
        }
    }

    public final double fetch(final Type type) {
        return this.multipliers.getOrDefault(type, 1.0);
    }
}
