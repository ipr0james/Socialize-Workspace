package net.thenova.socialize.util.response;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.thenova.socialize.util.task.TaskHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
public final class ResponseReaction {

    /**
     * List of all current ResponseReaction actions.
     */
    private static final List<ResponseReaction> MESSAGES = new ArrayList<>();

    private final Message message;
    private final Member owner;
    private final long ownerID;

    private final Map<String, Consumer<Member>> handlers = new HashMap<>();

    /* Whether other members other than the 'sender' should be able to react */
    private final boolean allowOthers;

    /* Mark whether the ResponseReaction is complete, determines if it should still be interacted with*/
    private boolean complete = false;

    private ResponseReaction(final Message message, final Member member, final boolean allowOthers) {
        this.message = message;
        this.owner = member;
        this.ownerID = member.getUser().getIdLong();

        this.allowOthers = allowOthers;
        ResponseReaction.MESSAGES.add(this);
    }

    /**
     * Add a Reaction to the Message. Creation of a Handler for that Reaction
     *
     * @param reaction - Reaction/Emoji being added
     * @param run - Consumer used for callback of the Member reacting
     * @return - Return this
     */
    public ResponseReaction reaction(final String reaction, final Consumer<Member> run) {
        this.message.addReaction(reaction.replace(">", "")).queue();
        this.handlers.put(reaction, run);

        return this;
    }

    /**
     * Making the ResponseReaction cancellable means that after 'seconds' the object will automatically timeout and complete itself.
     *
     * @param seconds - Period of time before timeout
     * @param runnable - Runnable acting as a callback for what to do once the task times out
     * @return - Return this
     */
    public ResponseReaction cancellable(final int seconds, final Runnable runnable) {
        TaskHandler.INSTANCE.scheduleSystemDelayed(() -> {
            if(!ResponseReaction.this.complete) {
                runnable.run();
                ResponseReaction.remove(ResponseReaction.this.message.getIdLong());
            }
        }, seconds, TimeUnit.SECONDS);

        return this;
    }

    /**
     * Called when a reaction is added to the specified message.
     *
     * @param member - Member reacting
     * @param emote - Emote being reacted with
     */
    public void handle(final Member member, final MessageReaction.ReactionEmote emote) {
        final String reaction = this.getEmoji(emote);

        if(this.handlers.containsKey(reaction)) {
            this.handlers.get(reaction).accept(member);
        }
    }

    /**
     * Set the ResponseReaction as complete and carry out tasks
     * - Set to 'complete' to stop other interactions using it
     * - Remove Emoji's
     */
    private void complete() {
        this.complete = true;
        this.message.clearReactions().queue();
    }

    /**
     * Gets the emoji of the response.
     * @param emote The emote object.
     * @return An emoji.
     */
    private String getEmoji(MessageReaction.ReactionEmote emote) {
        return emote.isEmote() ?
                emote.getEmote().getAsMention() :
                emote.getName();
    }

    /* Static Handlers */

    /**
     * Create a ResponseReaction object for use with GuildMessageReactionAddEvent.
     *
     * @param message - Message being reacted to
     * @param member - Member who executed the required react event
     * @param allowOthers - Whether other members are able to add a Reaction to the Message
     * @return - Return a blank ResponseReaction object
     */
    public static ResponseReaction create(final Message message, final Member member, final boolean allowOthers) {
        return new ResponseReaction(message, member, allowOthers);
    }

    /**
     * Fetch a ResponseReaction object based on the Message ID
     *
     * @param messageID - Message ID of the ResponseReaction to be fetched.
     * @return - Return the ResponseReaction object if present, else null
     */
    public static ResponseReaction fetch(final long messageID) {
        return ResponseReaction.MESSAGES.stream()
                .filter(message -> message.message.getIdLong() == messageID)
                .findFirst()
                .orElse(null);
    }

    /**
     * Remove a ResponseReaction object and mark is as completed
     * - Complete will stop any further GuildMessageReactionAddEvent's from triggering to this object.
     *
     * @param messageID - Message ID of the ResponseReaction to be removed.
     */
    public static void remove(final long messageID) {
        final ResponseReaction reaction = ResponseReaction.fetch(messageID);

        if(reaction != null) {
            reaction.complete();
            ResponseReaction.MESSAGES.remove(reaction);
        }
    }
}
