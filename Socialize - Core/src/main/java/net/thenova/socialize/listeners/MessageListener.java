package net.thenova.socialize.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.level.LevelManager;
import net.thenova.socialize.util.response.ResponseMessage;
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
public final class MessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
        final User user = event.getAuthor();

        if(user.isBot() || user.isFake()) {
            return;
        }

        final ResponseMessage response = ResponseMessage.fetch(event.getChannel().getIdLong(), user.getIdLong());

        if(response != null && !response.isComplete()) {
            response.handle(event.getMessage());
        } else {
            CommandMap.INSTANCE.parse(event);
            LevelManager.INSTANCE.message(event);
        }
    }
}
