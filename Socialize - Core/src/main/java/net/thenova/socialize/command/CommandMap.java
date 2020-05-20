package net.thenova.socialize.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.commands.CommandHelp;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.command.CommandEnabledChannelData;
import net.thenova.socialize.guild.data.entries.command.CommandGroupData;
import net.thenova.socialize.guild.data.entries.command.CommandPrefixData;
import net.thenova.socialize.guild.data.types.property.Property;
import net.thenova.titan.library.Titan;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public enum CommandMap {
    INSTANCE;

    private final List<Command> commands = new ArrayList<>();

    /**
     * Command loading in to live cache
     */
    public void load() {
        Arrays.stream(CommandCollection.values()).forEach(command -> this.commands.add(command.getCommand()));

        this.commands.add(new CommandHelp());
    }

    /**
     * Parse a GuildMessageReceivedEvent in to a Command and handle
     *
     * @param event - Event being fired
     */
    public final void parse(final GuildMessageReceivedEvent event) {
        final Guild guild = event.getGuild();
        final long guildID = event.getGuild().getIdLong();
        final long channelID = event.getChannel().getIdLong();

        final String message = event.getMessage().getContentRaw();
        final Member member = event.getMember();

        final String prefix = GuildHandler.INSTANCE.fetch(guildID, new CommandPrefixData())
                .get().asString();
        final List<Long> enabledChannels = GuildHandler.INSTANCE.fetch(guildID, new CommandEnabledChannelData()).contents()
                .stream()
                .map(Property::asLong)
                .collect(Collectors.toList());

        if(!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)
                || !message.startsWith(prefix)
                || member == null
                || (!enabledChannels.contains(channelID)
                    && !member.hasPermission(Permission.ADMINISTRATOR))) {
            return;
        }

        // TODO - Add check for 'setup completion'

        final String[] arguments = message.substring(prefix.length()).replaceAll(" +", " ").split(" ");
        final Command command = CommandMap.INSTANCE.getCommand(null, arguments[0]);

        if(command == null || !command.isEnabled()) {
            return;
        }

        final Set<CommandGroup> groups = CommandMap.INSTANCE.getCommandGroup(guildID, command);
        if((groups.isEmpty() || groups.stream().noneMatch(group -> group.getChannels().contains(channelID)))
                && !member.hasPermission(Permission.ADMINISTRATOR)) {
            final Set<Long> channels = new HashSet<>();
            groups.forEach(group -> group.getChannels().stream().filter(channel -> {
                final TextChannel txt = guild.getTextChannelById(channel);
                return txt != null && txt.canTalk(member);
            }).forEach(channels::add));

            event.getChannel().sendMessage(Embed.error(event.getMember())
                    .appendDescription("This command can only be used inside of: " + channels.stream().map(id -> "<#" + id + ">").collect(Collectors.joining(", ")))
                    .build())
                    .queue(msg -> {
                        Executors.newSingleThreadScheduledExecutor()
                                .schedule(() -> msg.delete().queue(), 5, TimeUnit.SECONDS);
                    });

            //TODO MESSAGE
            return;
        }

        final long current = System.currentTimeMillis();
        command.call(EntityHandler.INSTANCE.getEntity(member), CommandContext.build(prefix, arguments[0], event.getMessage(), Arrays.copyOfRange(arguments, 1, arguments.length)));
        Titan.INSTANCE.getDebug().info("Command '{}' called. Execution time: {}", message, System.currentTimeMillis() - current);
    }

    /**
     * Fetches a command from Command Set<> provided
     * - When command set is null use CommandMap.INSTANCE.commands Set<>
     *
     * @param commands - Command Set to be used for selection from
     * @param name - String name of the command being fetched. Name or Alias
     * @return - Return the Command object, null if command is not present
     */
    public final Command getCommand(List<Command> commands, final String name) {
        final String cmd = name.toLowerCase();
        commands = commands == null ? this.commands : commands;

        return commands.stream()
                .filter(command -> command.getName().toLowerCase().equals(cmd) || command.getAliases().contains(cmd))
                .findFirst()
                .orElse(null);
    }

    /**
     * Return the valid Set<CommandGroup> for the given commands.
     *
     * @param guildID - GuildID of the CommandGroups being fetched
     * @param command - Command for which CommandGroups to select
     * @return - Return Set<CommandGroup> of all valid command groups for the given guild & command
     */
    public final Set<CommandGroup> getCommandGroup(final long guildID, final Command command) {
        final CommandGroupData data = GuildHandler.INSTANCE.fetch(guildID, new CommandGroupData());

        return data.contents().entrySet().stream()
                .map(key -> new CommandGroup(key.getKey(), key.getValue().asJSON()))
                .filter(group -> group.getCommands().contains(command.getName().toLowerCase()))
                .collect(Collectors.toSet());
    }

    public String getPrefix(long guildID) {
        return GuildHandler.INSTANCE.fetch(guildID, new CommandPrefixData()).get().asString();
    }
}
