package net.thenova.socialize.guild.data.entries;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.types.TypeMap;
import net.thenova.titan.library.Titan;

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
public final class GuildChannelsData extends TypeMap {

    public enum Type {
        ERROR("errors"),
        LEVEL_UP("level_up"),
        LOUNGE("lounge"),
        GANG("gang"),
        LOG_GANG("log_gang"),
        LEADERBOARD_XP("leaderboard_xp"),
        LEADERBOARD_COINS("leaderboard_coins"),
        LEADERBOARD_GANG("leaderboard_gang");

        @Getter private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * Fetch a channel by a specific CHANNEL_ type
     *
     * @param type - Channel being fetched
     * @return - Return the channel by ID or null based on conditions
     */
    public TextChannel getChannel(final Type type) {
        return this.getChannel(type.toString());
    }

    /**
     * Fetch a channel by a specific CHANNEL_ type
     *
     * @param channel - Channel being fetched
     * @return - Return the channel by ID or null based on conditions
     */
    public TextChannel getChannel(final String channel) {
        final Guild guild = Bot.getJDA().getGuildById(super.guildID);
        if(guild == null) {
            Titan.INSTANCE.getLogger().info("[GuildChannelsData] - Guild returned null for id {}", super.guildID);
            return null;
        }

        final long channelID;
        try {
            channelID = super.get(channel).asLong();
        } catch (NumberFormatException ex) {
            if(channel.equals(Type.ERROR.toString())) {
                return guild.getSystemChannel();
            }
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve text channel for `" + channel + "`: ID is not set or invalid.");
            return null;
        }

        final TextChannel textChannel = guild.getTextChannelById(channelID);
        if(textChannel == null) {
            if(channel.equals(Type.ERROR.toString())) {
                return guild.getSystemChannel();
            }
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve text channel for `" + channel + "`: Channel could not be found");
            return null;
        }

        return textChannel;
    }

    public VoiceChannel getVoiceChannel(final String channel) {
        final Guild guild = Bot.getJDA().getGuildById(super.guildID);
        if(guild == null) {
            Titan.INSTANCE.getLogger().info("[GuildChannelsData] - Guild returned null for id {}", super.guildID);
            return null;
        }

        final long channelID;
        try {
            channelID = super.get(channel).asLong();
        } catch (NumberFormatException ex) {
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve text channel for `" + channel + "`: ID is not set or invalid.");
            return null;
        }

        final VoiceChannel voiceChannel = guild.getVoiceChannelById(channelID);
        if(voiceChannel == null) {
            GuildHandler.INSTANCE.messageError(this.guildID, "Failed to retrieve text channel for `" + channel + "`: Channel could not be found");
            return null;
        }

        return voiceChannel;
    }

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     */
    @Override
    protected final String getUniqueIdentifier() {
        return "channels";
    }
}
