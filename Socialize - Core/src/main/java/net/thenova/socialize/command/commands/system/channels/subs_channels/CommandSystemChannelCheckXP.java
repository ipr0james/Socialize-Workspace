package net.thenova.socialize.command.commands.system.channels.subs_channels;

import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.level.LevelManager;

import java.util.HashSet;
import java.util.Set;
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
        usage = "system channel checklxp [channel_id]",
        description = "Check whether experience gain is enabled within the current or a specified channel."
)
public final class CommandSystemChannelCheckXP extends Command {

    public CommandSystemChannelCheckXP() {
        super("checkxp");
    }

    @Override
    protected void execute(final Entity entity, final CommandContext context) {
        final Set<Long> channels = new HashSet<>();

        if(context.getArguments().length > 0) {
            try {
                channels.add(Long.parseLong(context.getArgument(0)));
            } catch (NumberFormatException ex) {
                context.error("Invalid channel ID provided.");
                return;
            }
        }

        if(channels.isEmpty()) {
            channels.add(context.getChannel().getIdLong());

            if(context.getMember().getVoiceState() != null
                    && context.getMember().getVoiceState().getChannel() != null) {
                channels.add(context.getMember().getVoiceState().getChannel().getIdLong());
            }
        }

        context.reply(Embed.socialize("Channel Experience status:\n" + channels.stream()
                    .map(id -> "**-** <#" + id + "> **-** " + (LevelManager.INSTANCE.isChannelEnabled(entity.getGuildID(), id) ? "enabled" : "disabled"))
                    .collect(Collectors.joining("\n")))).
                queue();
    }
}
