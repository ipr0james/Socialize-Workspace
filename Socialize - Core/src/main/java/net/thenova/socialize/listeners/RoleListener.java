package net.thenova.socialize.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import org.jetbrains.annotations.NotNull;

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
public final class RoleListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleAdd(@NotNull final GuildMemberRoleAddEvent event) {
        final Entity entity = EntityHandler.INSTANCE.getEntity(event.getMember());

        if(entity == null) {
            return;
        }

        entity.getMultiplier().check();
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull final GuildMemberRoleRemoveEvent event) {
        final Entity entity = EntityHandler.INSTANCE.getEntity(event.getMember());

        if(entity == null) {
            return;
        }

        entity.getMultiplier().check();
    }
}
