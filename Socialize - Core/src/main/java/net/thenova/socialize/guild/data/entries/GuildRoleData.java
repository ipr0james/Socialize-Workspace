package net.thenova.socialize.guild.data.entries;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.thenova.socialize.Bot;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.types.TypeMap;
import net.thenova.titan.library.Titan;

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
public final class GuildRoleData extends TypeMap {

    public enum Type {
        MEMBER_OF_THE_WEEK("motw"),
        DONOR("donor");

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * Fetch a channel by a specific CHANNEL_ type
     *
     * @param type - Channel being fetched
     * @return - Return the channel by ID or null based on conditions
     */
    public Role getRole(final Type type) {
        return this.getRole(type.toString());
    }

    /**
     * Fetch a role by a specific ROLE_ type
     *
     * @param role - Role being fetched
     * @return - Return the role by ID or null based on conditions
     */
    public Role getRole(final String role) {
        final Guild guild = Bot.getJDA().getGuildById(super.guildID);
        if(guild == null) {
            Titan.INSTANCE.getLogger().info("[GuildChannelsData] - Guild returned null for id {}", super.guildID);
            return null;
        }

        final long roleID;
        try {
            roleID = super.get(role).asLong();
        } catch (NumberFormatException ex) {
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve Role for `" + role + "`: ID is not set or invalid.");
            return null;
        }

        final Role roleObj = guild.getRoleById(roleID);
        if(roleObj == null) {
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve Role for `" + role + "`: Role could not be found");
            return null;
        }

        return roleObj;
    }

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     */
    @Override
    protected final String getUniqueIdentifier() {
        return "roles";
    }
}
