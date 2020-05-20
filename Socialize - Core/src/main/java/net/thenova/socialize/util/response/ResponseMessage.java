package net.thenova.socialize.util.response;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.thenova.socialize.util.task.TaskHandler;

import java.util.ArrayList;
import java.util.List;
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
public final class ResponseMessage {

    /**
     * List of all current ResponseMessage actions.
     */
    private static final List<ResponseMessage> MESSAGES = new ArrayList<>();

    private final Message message;
    private final Member owner;
    private final long ownerID;

    private final Consumer<Message> handler;

    private boolean complete = false;

    private ResponseMessage(final Message message, final Member owner, final Consumer<Message> handler) {
        this.message = message;

        this.owner = owner;
        this.ownerID = owner.getIdLong();

        this.handler = handler;

        ResponseMessage.MESSAGES.add(this);
    }

    /**
     * Making the ResponseMessage cancellable means that after 'seconds' the object will automatically timeout and complete itself.
     *
     * @param seconds - Period of time before timeout
     * @param runnable - Runnable acting as a callback for what to do once the task times out
     * @return - Return this
     */
    public ResponseMessage cancellable(final int seconds, final Runnable runnable) {
        TaskHandler.INSTANCE.scheduleSystemDelayed(() -> {
            if(!ResponseMessage.this.complete) {
                runnable.run();
                ResponseMessage.remove(ResponseMessage.this.message.getIdLong());
            }
        }, seconds, TimeUnit.SECONDS);

        return this;
    }

    /**
     * When a message is sent that meets the ResponseMessage criteria, call the handler to handle the message.
     *
     * @param message - Message sent
     */
    public void handle(final Message message) {
        this.handler.accept(message);
    }

    /**
     * Mark the ResponseMessage as complete so it will not be handled.
     */
    private void complete() {
        this.complete = true;
    }

    /* Static Handlers */

    /**
     * Create a ResponseMessage object for use with GuildMessageReceivedEvent.
     *
     * @param message - Message being reacted to
     * @param owner - Member who is used for the response
     * @param handler - What is to be done when the user messages
     * @return - Return a blank ResponseMessage object
     */
    public static ResponseMessage create(final Message message, final Member owner, final Consumer<Message> handler) {
        return new ResponseMessage(message, owner, handler);
    }

    /**
     * Fetch a ResponseMessage object based on the Message ID
     *
     * @param channelID - Channel where the message was sent.
     * @param ownerID - Owner of who needs to respond to the Message for a ResponseMessage to be present.
     * @return - Return the ResponseMessage object if present, else null
     */
    public static ResponseMessage fetch(final long channelID, final long ownerID) {
        return ResponseMessage.MESSAGES.stream()
                .filter(message -> message.message.getChannel().getIdLong() == channelID && message.ownerID == ownerID)
                .findFirst()
                .orElse(null);
    }

    /**
     * Remove a ResponseMessage object and mark is as completed
     * - Complete will stop any further GuildMessageReceivedEvent's from triggering to this object.
     *
     * @param messageID - Message ID of the ResponseMessage to be removed.
     */
    public static void remove(final long messageID) {
        final ResponseMessage reaction = ResponseMessage.MESSAGES.stream()
                .filter(message -> message.message.getIdLong() == messageID)
                .findFirst()
                .orElse(null);

        if(reaction != null) {
            reaction.complete();
            ResponseMessage.MESSAGES.remove(reaction);
        }
    }
}
