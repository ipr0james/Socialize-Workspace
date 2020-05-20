package net.thenova.socialize.listeners;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.thenova.socialize.Bot;
import net.thenova.socialize.util.response.ResponseReaction;
import net.thenova.titan.library.Titan;
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
public final class ReactionListener extends ListenerAdapter {

    private final long selfID;

    public ReactionListener() {
        this.selfID = Bot.getJDA().getSelfUser().getIdLong();
    }

    @Override
    public final void onGuildMessageReactionAdd(@NotNull final GuildMessageReactionAddEvent event) {
        final long userID = event.getUser().getIdLong();
        final ResponseReaction response;

        if(userID == this.selfID
                || (response = ResponseReaction.fetch(event.getMessageIdLong())) == null) {
            return;
        }

        if(response.isAllowOthers() || userID == response.getOwnerID()) {
            response.handle(event.getMember(), event.getReactionEmote());
        } else {
            event.getReaction().removeReaction(event.getUser())
                    .queue(null, ex ->
                            Titan.INSTANCE.getLogger().info("[ReactionEvent] - Emote could not be removed for ({} : {}) reaction to message {}",
                                event.getMember().getEffectiveName(), userID, event.getMessageId(), ex));
        }
    }
}
