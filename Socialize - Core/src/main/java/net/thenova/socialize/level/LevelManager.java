package net.thenova.socialize.level;

import de.arraying.kotys.JSONArray;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.thenova.socialize.Bot;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.entities.modules.EntityLevel;
import net.thenova.socialize.entities.modules.EntityMultiplier;
import net.thenova.socialize.entities.modules.stat_keys.CountText;
import net.thenova.socialize.entities.modules.stat_keys.CountVoice;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.level.LevelExperienceChannelData;
import net.thenova.socialize.guild.data.entries.level.LevelExperienceData;
import net.thenova.socialize.guild.data.entries.level.LevelExperienceData.Type;
import net.thenova.socialize.guild.data.entries.level.LevelRolesData;
import net.thenova.socialize.util.task.TaskHandler;
import net.thenova.titan.library.Titan;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
public enum LevelManager {
    INSTANCE;

    public static final int MAX_LEVEL = 101;

    public static final String KEY_CHAT = "next_message";
    public static final String KEY_VOICE = "voice_join_timestamp";

    private final List<Level> levels = new ArrayList<>();

    @Getter private boolean doubleXP = false;

    public void load() {
        long xp = 0;

        for(int i = 0; i <= MAX_LEVEL; i++) {
            this.levels.add(new Level(i, xp));
            xp += (5 * (i * i)) + (50 * i) + 100;
        }

        // Weekly Task - Monday
        final Calendar monday = Calendar.getInstance();
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        monday.set(Calendar.HOUR_OF_DAY, 0);
        monday.set(Calendar.MINUTE, 0);
        monday.set(Calendar.SECOND, 0);
        monday.set(Calendar.MILLISECOND, 0);

        final Calendar currentCalendar = Calendar.getInstance();
        if(!monday.getTime().after(currentCalendar.getTime())) {
            monday.add(Calendar.DATE, 7);
        }

        TaskHandler.INSTANCE.scheduleSystemRepeating(new TaskMOTW(),
                monday.getTimeInMillis() - System.currentTimeMillis(),
                TimeUnit.DAYS.toMillis(7),
                TimeUnit.MILLISECONDS);

        // Double XP Weekend - Handler

        final Calendar saturday = Calendar.getInstance();
        saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        saturday.set(Calendar.HOUR_OF_DAY, 0);
        saturday.set(Calendar.MINUTE, 0);
        saturday.set(Calendar.SECOND, 0);
        saturday.set(Calendar.MILLISECOND, 0);

        if(saturday.getTime().before(currentCalendar.getTime())
                && monday.getTime().after(currentCalendar.getTime())) {
            LevelManager.INSTANCE.doubleXP = true;
        }

        // Double XP Weekend - Enable task
        TaskHandler.INSTANCE.scheduleSystemRepeating(() ->
                        LevelManager.INSTANCE.doubleXP = true,
                saturday.getTimeInMillis() - System.currentTimeMillis(),
                TimeUnit.DAYS.toMillis(7),
                TimeUnit.MILLISECONDS);

        // Double XP Weekend - Disable task
        TaskHandler.INSTANCE.scheduleSystemRepeating(() ->
                        LevelManager.INSTANCE.doubleXP = false,
                monday.getTimeInMillis() - System.currentTimeMillis(),
                TimeUnit.DAYS.toMillis(7),
                TimeUnit.MILLISECONDS);

        // Experience Gain - User loading
        GuildHandler.INSTANCE.getGuilds().stream()
                .filter(id -> Bot.getJDA().getGuildById(id) != null)
                .map(id -> Bot.getJDA().getGuildById(id))
                .forEach(guild -> {
            assert guild != null;
            guild.getVoiceChannels()
                    .stream()
                    .filter(channel -> !channel.getMembers().isEmpty())
                    .forEach(channel ->
                            channel.getMembers()
                                    .forEach(member -> LevelManager.INSTANCE.voiceJoin(channel, member)));
        });
    }

    public void shutdown() {
        // Experience Gain - User unloading + saving
        GuildHandler.INSTANCE.getGuilds().stream()
                .filter(id -> Bot.getJDA().getGuildById(id) != null)
                .map(id -> Bot.getJDA().getGuildById(id)).forEach(guild -> {
            assert guild != null;
            guild.getVoiceChannels()
                    .stream()
                    .filter(channel -> !channel.getMembers().isEmpty())
                    .forEach(channel ->
                            channel.getMembers()
                                    .forEach(member -> this.memberUpdate(channel, member)));
        });
    }


    /**
     * Return the Level object for a given level.
     *
     * @param level - Level to be returned
     * @return - Return level object of given level
     */
    public final Level getLevel(final int level) {
        return this.levels.get(level);
    }

    /**
     * Fetch a level based around experience, returning their highest possible level based on experience.
     *
     * @param experience - Experience of level to be returned
     * @return - Return the Level object
     */
    public final Level getLevelByXP(final long experience) {
        return this.levels.stream()
                .filter(level -> level.getRequirement() <= experience)
                .max(Comparator.comparing(Level::getRequirement))
                .orElse(null);
    }

    /**
     * Return roles for a given Level for a specific guild
     *
     * @param guildID - Guild ID
     * @param level - Level
     * @return - Return Set<> of roles
     */
    public final Set<Role> getLevelRoles(final long guildID, final long level) {
        final LevelRolesData data = GuildHandler.INSTANCE.fetch(guildID, new LevelRolesData());
        final Set<Role> roles = new HashSet<>();

        if(data.contains(String.valueOf(level))) {
            final Guild guild = Bot.getJDA().getGuildById(guildID);
            assert guild != null;

            final JSONArray array = data.get(String.valueOf(level)).asArray();

            for(int i = 0; i < array.length(); i++) {
                final Role role = guild.getRoleById(array.string(i));

                if(role != null) {
                    roles.add(role);
                } else {
                    final String val = array.string(i);
                    array.delete(i);
                    data.save();

                    Titan.INSTANCE.getDebug().info("[LevelManager] - {} has been removed from roles for level {} in guild {} as it was invalid",
                            val, level, guildID);
                    //TODO - Test
                }
            }
        }

        return roles;
    }

    /**
     *
     *
     * @param guildID
     * @param level
     * @return
     */
    public final Set<Role> getAllLevelRoles(final long guildID, final long level) {
        final LevelRolesData data = GuildHandler.INSTANCE.fetch(guildID, new LevelRolesData());
        final Set<Role> roles = new HashSet<>();
        final Guild guild = Bot.getJDA().getGuildById(guildID);
        assert guild != null;

        for(long i = level; i > 0; i--) {
            if(data.contains(String.valueOf(level))) {
                final JSONArray array = data.get(String.valueOf(level)).asArray();

                for(int j = 0; j < array.length(); j++) {
                    final Role role = guild.getRoleById(array.string(j));

                    if(role != null) {
                        roles.add(role);
                    } else {
                        final String val = array.string(j);
                        array.delete(j);
                        data.save();

                        Titan.INSTANCE.getDebug().info("[LevelManager] - {} has been removed from roles for level {} in guild {} as it was invalid",
                                val, level, guildID);
                        //TODO - Test
                    }
                }
            }
        }
        return roles;
    }

    /**
     * Handle experience/coin gain when a message is sent by a user.
     *
     * @param event - Event for when Member sends a guild message
     */
    @SuppressWarnings("ConstantConditions")
    public final void message(@NotNull GuildMessageReceivedEvent event) {
        if(!LevelManager.INSTANCE.isChannelEnabled(event.getGuild().getIdLong(), event.getChannel().getIdLong())
                || event.getMember().getVoiceState().getChannel() != null) {
            return;
        }

        final Entity entity = EntityHandler.INSTANCE.getEntity(event.getMember());
        if(entity.getValidation().contains(LevelManager.KEY_CHAT)
                && entity.getValidation().get(LevelManager.KEY_CHAT, Long.class) > System.currentTimeMillis()) {
            return;
        }

        final LevelExperienceData data = GuildHandler.INSTANCE.fetch(event.getGuild().getIdLong(), new LevelExperienceData());

        final int xp = (int) Math.floor(data.get(Type.XP_CHAT).asInt()
                * entity.getMultiplier().fetch(EntityMultiplier.Type.EXPERIENCE))
                * (this.doubleXP ? 2 : 1);
        final int coins = (int) Math.floor((data.get(Type.COINS_CHAT).asInt()
                * entity.getMultiplier().fetch(EntityMultiplier.Type.COINS)));


        entity.getLevel().add(xp, EntityLevel.Reason.SYSTEM);
        entity.getBalance().add(coins, EntityBalance.Reason.SYSTEM);

        entity.getStats().increment(new CountText());

        entity.getValidation().set(LevelManager.KEY_CHAT, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(data.get(Type.CHAT_DELAY).asInt()));
    }

    /**
     * Complete checks when a member joins a VoiceChannel
     * 1. Check that the voice channel has Experience/Coin gain enabled.
     * 2. Check that the number of members in the VC is 2 or more.
     * 3. Add the logging key for any members that currently don't have then {@link LevelManager KEY_VOICE}
     *
     * @param channel - VoiceChannel being joined
     */
    public final void voiceJoin(final VoiceChannel channel, final Member joiner) {
        if(LevelManager.INSTANCE.isChannelEnabled(channel.getGuild().getIdLong(), channel.getIdLong())
            || joiner.getUser().isBot()
                || (joiner.getVoiceState() != null && joiner.getVoiceState().isMuted())) {
            return;
        }

        // Set of valid members
        final Set<Member> members = channel.getMembers()
                .stream()
                .filter(member -> !member.getUser().isBot()
                        && member.getVoiceState() != null
                        && !member.getVoiceState().isMuted())
                .collect(Collectors.toSet());

        // No need to do anything if there is only 1 VALID member nor if members are already being logged
        if(members.size() < 2) {
            return;
        }

        members.forEach(member -> {
            final Entity entity = EntityHandler.INSTANCE.getEntity(member);
            if(entity != null && !entity.getValidation().contains(LevelManager.KEY_VOICE)) {
                entity.getValidation().set(LevelManager.KEY_VOICE, System.currentTimeMillis());
            }
        });
    }

    /**
     * Complete checks when a member leaves a voice channel
     *
     * @param channel - VoiceChannel being leaved
     * @param leaver - Member leaving the voice channel
     */
    public final void voiceLeave(final VoiceChannel channel, final Member leaver) {
        if(LevelManager.INSTANCE.isChannelEnabled(channel.getGuild().getIdLong(), channel.getIdLong())
            || leaver.getUser().isBot()) {
            return;
        }

        // Set of valid members
        final Set<Member> members = channel.getMembers()
                .stream()
                .filter(member -> !member.getUser().isBot()
                        && member.getVoiceState() != null
                        && !member.getVoiceState().isMuted()
                        && member.getUser().getIdLong() != leaver.getUser().getIdLong())
                .collect(Collectors.toSet());

        // Handle the member leaving
        LevelManager.INSTANCE.memberUpdate(channel, leaver);

        // Handle all other current members, stop logging them if there is not enough members
        if(members.size() < 2) {
            members.forEach(member -> LevelManager.INSTANCE.memberUpdate(channel, member));
        }
    }

    /**
     * Update a member from a VC when requirements are met.
     * 1. User leaves VC
     * 2. Member is left alone in VC after a member leaves
     * Complete:
     * 1. Check the Entity status that they didn't become null (kicked/left from discord while in VC)
     * 2. Check the Entity was being tracked for Voice
     * 3. Determine their XP/Coin values and increment their current accounts
     *
     * @param channel - Channel the member is in/has left
     * @param member - Member
     */
    private void memberUpdate(final VoiceChannel channel, final Member member) {
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);

        if(entity == null) {
            Titan.INSTANCE.getLogger().info("[LevelManager] - Entity was null when leaving voice channel {} : {}", channel.getIdLong(), channel.getName());
            return;
        }

        if(entity.getValidation().contains(LevelManager.KEY_VOICE)) {
            final long duration = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - entity.getValidation().get(LevelManager.KEY_VOICE, Long.class));

            if(duration < 1) {
                return;
            }

            final LevelExperienceData data = GuildHandler.INSTANCE.fetch(channel.getGuild().getIdLong(), new LevelExperienceData());

            final int xp = (int) Math.floor(data.get(Type.XP_VOICE).asInt()
                    * entity.getMultiplier().fetch(EntityMultiplier.Type.EXPERIENCE)
                    * duration)
                    * (this.doubleXP ? 2 : 1);
            final int coins = (int) Math.floor((data.get(Type.COINS_VOICE).asInt()
                    * entity.getMultiplier().fetch(EntityMultiplier.Type.COINS))
                    * duration);

            entity.getLevel().add(xp, EntityLevel.Reason.SYSTEM);
            entity.getBalance().add(coins, EntityBalance.Reason.SYSTEM);

            entity.getStats().increment(new CountVoice(), duration);
            entity.getValidation().unset(LevelManager.KEY_VOICE);
        }
    }

    /**
     * Toggle experience gain in a channel
     *
     * @param guildID - Guild ID
     * @param channelID - Channel ID
     * @param value - Whether the channel is being enabled or disabled for experience/coin gain
     */
    public void toggleChannel(final long guildID, final long channelID, final boolean value) {
        final LevelExperienceChannelData data = GuildHandler.INSTANCE.fetch(guildID, new LevelExperienceChannelData());

        if(value) {
            if(data.contents().stream().noneMatch(prop -> prop.asLong() == channelID)) {
                data.add(channelID);
            }
        } else {
            if(data.contents().stream().anyMatch(prop -> prop.asLong() == channelID)) {
                data.remove(channelID);
            }
        }
    }

    /**
     * Check whether a specific channel has experience/coin gain enabled.
     *
     * @param guildID - Guild ID
     * @param channelID - Channel ID
     * @return - Return whether XP gain is enabled.
     */
    public boolean isChannelEnabled(final long guildID, final long channelID) {
        return GuildHandler.INSTANCE.fetch(guildID, new LevelExperienceChannelData()).contents()
                .stream()
                .noneMatch(prop -> prop.asLong() == channelID);
    }
}
