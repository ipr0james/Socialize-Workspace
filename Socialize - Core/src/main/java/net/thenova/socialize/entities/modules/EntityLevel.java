package net.thenova.socialize.entities.modules;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.stat_keys.experience.PrestigeCurrent;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPCurrent;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPTotal;
import net.thenova.socialize.entities.modules.stat_keys.experience.XPWeek;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.level.Level;
import net.thenova.socialize.level.LevelManager;
import net.thenova.socialize.level.prestige.PrestigeManager;

import java.util.List;
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
public final class EntityLevel {

    public enum Reason {
        ADMIN,
        SYSTEM
    }

    private final Entity entity;
    private final EntityStats stats;

    @Getter private Level level;

    public EntityLevel(final Entity entity) {
        this.entity = entity;
        this.stats = entity.getStats();

        this.level = LevelManager.INSTANCE.getLevelByXP(this.stats.fetch(new XPCurrent()));
        this.checkRoles();
    }

    /**
     * Add experience to an Entity by a provided amount. Check if the Entity has levelled up.
     *
     * @param amount - Amount to be added
     */
    public final void add(final long amount, final Reason reason) {
        this.stats.increment(new XPCurrent(), amount);
        this.stats.increment(new XPTotal(), amount);

        switch(reason) {
            case SYSTEM:
                this.stats.increment(new XPWeek(), amount);
        }

        this.checkLevel();
    }

    /**
     * Take experience from an Entity by a provided amount. Check if the Entity has de-levelled.
     *
     * @param amount - Amount to be deducted
     */
    public final void take(long amount, final Reason reason) {
        final long current = this.stats.fetch(new XPCurrent());
        amount = current - amount < 0 ? 0 : current - amount;

        this.stats.update(new XPCurrent(), amount);
        this.checkLevel();
    }

    /**
     * Set users experience, check level based on gain/deduct
     *
     * @param amount - Amount being set
     */
    public final void set(final long amount, final Reason reason) {
        this.stats.update(new XPCurrent(), amount < 0 ? 0 : amount);

        this.checkLevel();
    }

    /**
     * Return the users current Experience Long
     *
     * @return - XP value
     */
    public final Long getXP() {
        return this.stats.fetch(new XPCurrent());
    }

    /**
     * Check if a member is currently missing any of the valid level roles for their current level.
     */
    private void checkRoles() {
        final Guild guild = Bot.getJDA().getGuildById(this.entity.getGuildID());
        if(guild == null) {
            return;
        }

        final Member member = guild.getMemberById(this.entity.getUserID());
        if(member == null) {
            //LevelManager.INSTANCE.getLogger().info("[EntityLevel] - Failed to update roles for {} as entity has left the server.", this.entity.getID());
            return;
        }

        final Set<Role> roles = LevelManager.INSTANCE.getAllLevelRoles(this.entity.getGuildID(), this.level.getLevel());
        final List<Role> mRoles = member.getRoles();
        final Set<Role> missing = mRoles.stream()
                .filter(role -> roles.stream().noneMatch(r -> r.getIdLong() == role.getIdLong()))
                .collect(Collectors.toSet());

        final long prestige;
        if((prestige = this.entity.getStats().fetch(new PrestigeCurrent())) > 0) {
            final Set<Role> pRoles = PrestigeManager.INSTANCE.getAllPrestigeRoles(this.entity.getGuildID(), prestige);
            missing.addAll(mRoles.stream()
                    .filter(role -> pRoles.stream().noneMatch(r -> r.getIdLong() == role.getIdLong()))
                    .collect(Collectors.toSet()));
        }

        if(missing.isEmpty()) {
            return;
        }

        // Add missing roles, do not remove others.
        missing.forEach(role -> guild.addRoleToMember(member, role).queue());
    }

    /**
     * Check members XP to see if their level has adjusted:
     * 1. Check if they are still the current level, return if so
     * 2. Check if they have de-ranked, handle removing ranks
     * 3. Check if they have ranked up, handle adding ranks and announcing if necessary.
     */
    private void checkLevel() {
        if(this.level.getNextLevel() == null) {
            this.prestige();
            return;
        }

        final long experience = this.stats.fetch(new XPCurrent());
        if(this.level.getRequirement() > experience) {
            //TODO - Handle removing roles here?
            this.level = this.level.getPreviousLevel();

            this.checkLevel();
        } else if (this.level.getNextLevel().getRequirement() < experience) {
            //TODO - Check this only handles when they level up
            this.level = this.level.getNextLevel();

            final Set<Role> roles = LevelManager.INSTANCE.getLevelRoles(this.entity.getGuildID(), this.level.getLevel());
            if(!roles.isEmpty()) {
                final Guild guild = Bot.getJDA().getGuildById(this.entity.getGuildID());
                final Member member;
                if(guild == null || (member = guild.getMemberById(this.entity.getUserID())) == null) {
                    return;
                }

                guild.modifyMemberRoles(member, roles, null).queue();
                final Role role = roles.stream()
                        .filter(r -> r.getName().toLowerCase().contains("level"))
                        .findFirst()
                        .orElse(null);
                if(role != null) {
                    final TextChannel channel = GuildHandler.INSTANCE
                            .fetch(this.entity.getGuildID(), new GuildChannelsData())
                            .getChannel(GuildChannelsData.Type.LEVEL_UP);

                    if(channel == null) {
                        return;
                    }

                    final EmbedBuilder builder = Embed.def();

                    builder.setColor(role.getColor());
                    builder.appendDescription("You are now <@&" + role.getIdLong() + ">! Congratulations!");

                    channel.sendMessageFormat(this.entity.asMention()).queue();
                    channel.sendMessage(builder.build()).queue();
                }
            }

            this.checkLevel();
        }
    }

    private void prestige() {
        final Guild guild = Bot.getJDA().getGuildById(this.entity.getGuildID());
        final Member member;
        if(guild == null || (member = guild.getMemberById(this.entity.getUserID())) == null) {
            return;
        }

        if(this.entity.getStats().fetch(new PrestigeCurrent()) + 1 > PrestigeManager.INSTANCE.getMaxPrestige(this.entity.getGuildID())) {
            return;
        }

        // Prestige actions TODO CHECK THIS IS ALL
        this.level = LevelManager.INSTANCE.getLevel(0);
        this.stats.update(new XPCurrent(), 0L);
        this.stats.increment(new PrestigeCurrent());

        final Set<Role> take = LevelManager.INSTANCE.getAllLevelRoles(this.entity.getGuildID(), LevelManager.MAX_LEVEL);
        LevelManager.INSTANCE.getLevelRoles(this.entity.getGuildID(), 0L).forEach(take::remove);
        take.forEach(role -> guild.removeRoleFromMember(member, role).queue());

        this.checkRoles();

        // Chat message
        final TextChannel channel = GuildHandler.INSTANCE
                .fetch(this.entity.getGuildID(), new GuildChannelsData())
                .getChannel(GuildChannelsData.Type.LOUNGE);

        if(channel == null) {
            return;
        }

        final EmbedBuilder builder = Embed.socialize();

        builder.appendDescription("Congratulations you have reached **Prestige " + this.stats.fetch(new PrestigeCurrent()) + "**");

        channel.sendMessageFormat(this.entity.asMention()).queue();
        channel.sendMessage(builder.build()).queue();

    }
}
