package net.thenova.socialize.command.commands.system.channels.subs_channels;

import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;

import java.util.Arrays;
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
        min = 2,
        usage = "system channel settype {type} {channel_id}",
        description = "Set a Channel Type to a given ID."
)
public final class CommandSystemChannelSetType extends Command {

    public CommandSystemChannelSetType() {
        super("settype", "set");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final GuildChannelsData.Type type;
        try {
            type = GuildChannelsData.Type.valueOf(context.getArgument(0).toUpperCase());
        } catch (IllegalArgumentException ex) {
            context.error("Invalid Channel type. Types: " + Arrays.stream(GuildChannelsData.Type.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
            return;
        }

        final long id;
        try {
            id = Long.parseLong(context.getArgument(1));
        } catch (NumberFormatException ex) {
            context.error("Channel ID must be numerical.");
            return;
        }

        if(context.getGuild().getTextChannelById(id) == null) {
            context.error("Channel does not exist.");
            return;
        }

        GuildHandler.INSTANCE.fetch(context.getGuild().getIdLong(), new GuildChannelsData()).set(type.toString(), id);
        context.reply(Embed.socialize("<#" + id + "> has been updated as the ID for " + type.name() + ".")).queue();
    }
}
