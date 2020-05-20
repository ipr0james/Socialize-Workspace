package net.thenova.socialize.command.commands.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.util.response.ResponseMessage;

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
public final class CommandSystemStart extends Command {

    public CommandSystemStart() {
        super("start");

        this.addPermission(
                CommandPermission.discord(Permission.ADMINISTRATOR)
        );
    }

    @Override
    protected void execute(Entity entity, CommandContext context) {
        final EmbedBuilder embed = Embed.socialize();

        embed.appendDescription("Welcome to Socialize Core. \nPlease enter the ID of a channel for all logging information can be sent for errors/un-configured components.");

        context.reply(embed.build()).queue(message -> {
            ResponseMessage.create(message, context.getMember(), response -> {
                final long id;
                try {
                    id = Long.parseLong(response.getContentRaw());
                } catch (NumberFormatException ex) {
                    message.editMessage(Embed.error(context.getMember(), "Channel ID must be numerical.")).queue();
                    return;
                }

                final TextChannel channel = context.getGuild().getTextChannelById(id);

                if(channel == null) {
                    message.editMessage(Embed.error(context.getMember(), "Invalid Text Channel, please try again.")).queue();
                } else {
                    GuildHandler.INSTANCE.fetch(context.getGuild().getIdLong(), new GuildChannelsData()).set(GuildChannelsData.Type.ERROR.toString(), channel.getIdLong());
                    message.editMessage(Embed.socialize("Configuration completed.").build()).queue();
                }
            }).cancellable(300, () -> {
                message.editMessage(Embed.error(context.getMember(), "Configuration has timed out. Please start again.")).queue();
            });
        });
    }
}
