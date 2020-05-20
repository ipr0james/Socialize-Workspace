package net.thenova.socialize.command.permission;

import de.arraying.kotys.JSON;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;

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
public final class CommandPermission {

    private final PermissionType type;
    private final Object value;

    /**
     * Creates a new Discord permission (everyone can use).
     * @return A permission.
     */
    public static CommandPermission discord() {
        return new CommandPermission(PermissionType.DISCORD, Permission.MESSAGE_WRITE);
    }

    /**
     * Creates a new Discord permission.
     * @param permission The Discord permission.
     * @return A permission.
     */
    public static CommandPermission discord(Permission permission) {
        return new CommandPermission(PermissionType.DISCORD, permission);
    }

    /**
     * Creates a new per-guild role permission.
     * @param field The key which will get a value of a role ID or role name.
     * @return A permission.
     */
    public static CommandPermission role(String field) {
        return new CommandPermission(PermissionType.ROLE, field);
    }

    /**
     * Creates a new developer only permission, with an extra permission on top of that.
     * @param onTopOfThat The extra permission.
     * @return A permission.
     */
    public static CommandPermission developer() {
        return new CommandPermission(PermissionType.DEVELOPER, null);
    }

    /**
     * Creates the permission.
     * @param type The type of permission.
     * @param value The permission value.
     */
    private CommandPermission(PermissionType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public CommandPermission(final JSON json) {
        this.type = null;
        this.value = null;
        //TODO
    }

    /**
     * Whether or not the member has permission.
     * @return True if they do, false otherwise.
     */
    public boolean hasPermission(final Member member, final GuildChannel channel) {
        switch(type) {
            case DISCORD:
                return member.hasPermission(channel, (Permission) value);
            case ROLE: {
                return false;
                /*String data = DataContainer.INSTANCE.getGravity().load(new GuildData(channel.getGuild().getId()))
                        .get(value.toString())
                        .asString();
                return (data != null && member.getRoles().stream()
                        .anyMatch(it -> it.getName().toLowerCase()
                                .contains(data.toLowerCase()) || it.getId().equalsIgnoreCase(data)))
                        || member.hasPermission(Permission.ADMINISTRATOR);*/
            }
            case DEVELOPER:
                return member.getUser().getIdLong() == Bot.DEVELOPER_ID;
            default:
                break;
        }
        throw new IllegalStateException("Permission not exhaustive");
    }
}
