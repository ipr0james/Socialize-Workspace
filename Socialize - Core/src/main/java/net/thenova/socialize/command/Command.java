package net.thenova.socialize.command;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.command.CommandDisabledCommandData;
import net.thenova.socialize.guild.data.entries.command.CommandPermissionData;

import java.util.*;
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
@SuppressWarnings("WeakerAccess")
@Getter
public abstract class Command {

    protected final String name;
    private final Set<String> aliases = new HashSet<>();

    protected final List<Command> subCommands = new ArrayList<>();

    protected final Set<CommandPermission> permissions = new HashSet<>();
    protected CommandUsage usage;

    @Setter
    private boolean enabled = true;

    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases.addAll(Arrays.asList(aliases));

        if(this.getClass().isAnnotationPresent(CommandUsage.class)) {
            this.usage = getClass().getAnnotation(CommandUsage.class);
        }
    }

    /**
     * Call command, check for sub commands then execute.
     *
     * @param entity - Entity calling the command
     * @param context - Command information
     */
    public final void call(final Entity entity, final CommandContext context) {
        if(GuildHandler.INSTANCE.fetch(context.getGuild().getIdLong(), new CommandDisabledCommandData()).contents()
                .stream().anyMatch(property -> property.asString().equals(this.name))) {
            context.error("This command is currently disabled.");
            return;
        }

        final List<CommandPermission> permissions = GuildHandler.INSTANCE.fetch(entity.getGuildID(), new CommandPermissionData())
                .contents()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(this.name))
                .map(entry -> new CommandPermission(entry.getValue().asJSON()))
                .collect(Collectors.toList());
        permissions.addAll(this.permissions);
        if(entity.getUserID() != Bot.DEVELOPER_ID && !permissions.stream().allMatch(permission -> permission.hasPermission(context.getMember(), (GuildChannel) context.getChannel()))) {
            context.reply("\uD83D\uDE20");
            return;
        }

        if(this.usage != null && context.getArguments().length < this.usage.min()) {
            context.error("**Usage:** `" + context.getPrefix() + this.usage.usage() + "`");
            return;
        }

        final String[] args = context.getArguments();
        if (!this.subCommands.isEmpty() && args.length > 0) {
            final Command subCommand = CommandMap.INSTANCE.getCommand(this.subCommands, args[0]);
            if (subCommand != null) {
                subCommand.call(entity, context.clone(args[0].toLowerCase(), Arrays.copyOfRange(args, 1, args.length)));
                return;
            }
        }

        this.execute(entity, context);
    }

    /**
     * Execution method for the command once 'run' has completed execution checks.
     *
     * @param entity - Entity executing commands
     * @param context - Commands context
     */
    protected abstract void execute(final Entity entity, final CommandContext context);

    /**
     * Add SubCommands to the current command.
     *
     * @param subCommands - SubCommands being added
     */
    protected final void addSubCommand(final Command... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    /**
     * Add Permission to the current command.
     *
     * @param permissions - Permissions to be added
     */
    protected final void addPermission(final CommandPermission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

}
