package net.thenova.socialize.command.commands.system.channels.subs_channels;

import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.level.LevelManager;

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
        min = 1,
        usage = "system channel togglexp {channel_id/vc/tc} [value]",
        description = "Toggle whether a channel has experience gain enabled."
)
public final class CommandSystemChannelToggleXP extends Command {

    public CommandSystemChannelToggleXP() {
        super("togglexp");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final Member member = context.getMember();
        final String[] args = context.getArguments();

        final long channelID;
        if(args[0].equalsIgnoreCase("vc")) {
            if(member.getVoiceState() != null && member.getVoiceState().getChannel() != null) {
                channelID = member.getVoiceState().getChannel().getIdLong();
            } else {
                context.error("You are not currently in a Voice Channel.");
                return;
            }
        } else if(args[0].equalsIgnoreCase("tc")) {
            channelID = context.getChannel().getIdLong();
        } else {
            try {
                channelID = Long.parseLong(args[0]);
                if(!GuildHandler.INSTANCE.isChannel(context.getGuild().getIdLong(), channelID)) {
                    context.reply("The provided channel (%d) does not exist.", channelID);
                    return;
                }
            } catch (NumberFormatException ex) {
                context.error("Invalid channel ID provided.");
                return;
            }
        }

        final boolean value;
        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                value = Boolean.parseBoolean(args[1]);
            } else {
                context.error("Invalid toggle value (%s)", args[1]);
                return;
            }
        } else {
            value = !LevelManager.INSTANCE.isChannelEnabled(context.getGuild().getIdLong(), channelID);
        }

        LevelManager.INSTANCE.toggleChannel(entity.getGuildID(), channelID, value);
        context.reply(Embed.socialize().appendDescription("Experience gain has been "
                + (LevelManager.INSTANCE.isChannelEnabled(entity.getGuildID(), channelID) ? "enabled" : "disabled") + " in <#" + channelID + ">")).queue();
    }
}
