package net.thenova.socialize.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandCollection;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.library.util.pagination.Page;
import net.thenova.titan.library.util.pagination.Pagination;

import java.util.Arrays;
import java.util.List;
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
@CommandUsage(
        min = 0,
        usage = "help [page]",
        description = "Display this information."
)
public final class CommandHelp extends Command {

    public CommandHelp() {
        super("help");
    }

    @Override
    protected void execute(Entity entity, CommandContext context) {
        final Pagination<Command> pagination = new Pagination<>(Arrays.stream(CommandCollection.values())
                .map(CommandCollection::getCommand)
                .collect(Collectors.toList()), 5);
        int pageNumber = 1;
        final String[] args = context.getArguments();

        if(args.length > 0) {
            if(!UNumber.isInt(args[0])) {
                context.error("Page value must be numerical");
                return;
            } else {
                pageNumber = Integer.parseInt(args[0]);
                if(pagination.total() < pageNumber) {
                    context.error("The given page number exceeds the number of pages.");
                    return;
                }
            }
        }

        final EmbedBuilder builder = Embed.def();
        builder.setTitle("Help Menu (" + pageNumber + "/" + pagination.total() + ")");
        builder.setThumbnail(Bot.getJDA().getSelfUser().getEffectiveAvatarUrl());

        final String prefix = context.getPrefix();
        final List<Page<Command>> page = pagination.page(pageNumber);
        page.forEach(value -> {
            final Command command = value.getValue();
            final CommandUsage usage = command.getUsage();

            final StringBuilder description = new StringBuilder();

            if(usage != null) {
                description.append("`").append(usage.description()).append("`\n");
                description.append("**Usage:** ").append(context.getPrefix()).append(usage.usage()).append("\n");
                description.append("**Alias:** ")
                        .append(command.getAliases().isEmpty() ? "N/A" : prefix +
                                String.join(", " + prefix, command.getAliases()));
            }

            description.append("\n").append(Embed.Z);

            builder.addField("**Command:** " + prefix + command.getName(), description.toString(), false);
        });

        if(pageNumber < pagination.total()) {
            builder.setFooter("Use " + prefix + "help " + (pageNumber+1) + " to get to the next page!", null);
        }

        context.reply(builder.build()).queue();
    }
}