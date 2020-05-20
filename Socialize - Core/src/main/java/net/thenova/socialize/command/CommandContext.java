package net.thenova.socialize.command;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.thenova.socialize.Embed;

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
public final class CommandContext {

    //TODO - MAJOR TIDY UP ??!!?!?!?!?

    private final Message message;
    private final MessageChannel channel;
    private final Guild guild;
    private final Member member;

    private final String prefix;
    private final String label;
    private final String[] arguments;

    /**
     * Creates a new command context. Which is essentially metadata regarding command execution.
     *
     * @param prefix - Command prefix
     * @param message - The message
     * @param arguments - The arguments
     */
    private CommandContext(String prefix, String label, Message message, String[] arguments) {
        this.message = message;
        this.channel = message.getChannel();
        this.guild = message.getGuild();
        this.member = message.getMember();
        this.arguments = arguments;
        this.prefix = prefix;
        this.label = label;
    }

    /**
     * Constructs a command context by wrapping the constructor.
     *
     * @param prefix - Command prefix
     * @param message - The message
     * @param args - The arguments
     * @return - A command context
     */
    public static CommandContext build(String prefix, String label, Message message, String[] args) {
        return new CommandContext(prefix, label, message, args);
    }

    /**
     * Replies to the context.
     *
     * @param message - The message
     * @param format - Any message formats
     */
    public void reply(String message, Object... format) {
        this.channel.sendMessageFormat(message, format)
                .queue(null, Throwable::printStackTrace);
    }

    /**
     * Replies to the context.
     *
     * @param message - A message embed
     */
    public MessageAction reply(MessageEmbed message) {
        return this.channel.sendMessage(message);
    }

    /**
     * Replies to the context.
     *
     * @param message - A message embed
     */
    public MessageAction reply(EmbedBuilder message) {
        return this.channel.sendMessage(message.build());
    }

    /**
     * Replies to the context using a default error configuration
     *
     * @param message - Message tro be appended to description
     */
    public void error(String message, Object... format) {
        this.channel.sendMessage(Embed.error(member)
                    .appendDescription(String.format(message, format))
                    .build())
                .queue(null, Throwable::printStackTrace);
    }

    /**
     * Clones the command context with new arguments, for subcommands.
     *
     * @param arguments - The new arguments
     * @return - A new command context
     */
    CommandContext clone(String subLabel, String[] arguments) {
        return new CommandContext(this.prefix, this.label + " " + subLabel, this.message, arguments);
    }

    /**
     * Return a specific argument
     *
     * @param pos - Position in Array
     * @return - Argument
     */
    public String getArgument(int pos) {
        return this.arguments[pos];
    }
}
