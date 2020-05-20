package net.thenova.socialize.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.thenova.socialize.level.LevelManager;
import net.thenova.titan.library.Titan;
import org.jetbrains.annotations.NotNull;

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
public final class VoiceListener extends ListenerAdapter {

    /**
     * When a member joins a voice channel.
     *
     * @param event The event instance
     */
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        LevelManager.INSTANCE.voiceJoin(event.getChannelJoined(), event.getMember());
    }

    /**
     * Fired when a user moves from one voice chat to another.
     *
     * @param event - Event when user moves from one guild voice chat to another
     */
    @Override
    public void onGuildVoiceMove(@NotNull final GuildVoiceMoveEvent event) {
        final Member member = event.getMember();

        LevelManager.INSTANCE.voiceLeave(event.getChannelLeft(), member);
        LevelManager.INSTANCE.voiceJoin(event.getChannelJoined(), member);
    }

    /**
     * Tracking when a user mutes themselves, to stop xp gain. Resume XP gain when they're un-muted.
     *
     * @param event - The event instance
     */
    @Override
    public void onGuildVoiceMute(@NotNull final GuildVoiceMuteEvent event) {
        final Member member = event.getMember();
        final VoiceChannel channel = event.getVoiceState().getChannel();

        if(channel == null) {
            Titan.INSTANCE.getLogger().info("[VoiceListener] [onMute] - User ({} : {}) muted and channel was null",
                    member.getUser().getId(), member.getEffectiveName());
            return;
        }

        if(member.getVoiceState() != null && member.getVoiceState().isMuted()) {
            LevelManager.INSTANCE.voiceLeave(channel, member);
        } else {
            LevelManager.INSTANCE.voiceJoin(channel, member);
        }
    }

    /**
     * When a member leaves a voice channel.
     *
     * @param event The event instance
     */
    @Override
    public void onGuildVoiceLeave(@NotNull final GuildVoiceLeaveEvent event) {
        LevelManager.INSTANCE.voiceLeave(event.getChannelLeft(), event.getMember());
    }
}
