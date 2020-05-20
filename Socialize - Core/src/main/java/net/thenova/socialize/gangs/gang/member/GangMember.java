package net.thenova.socialize.gangs.gang.member;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.stat_keys.StatKey;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangDMs;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangSuffix;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.util.UString;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

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
public final class GangMember {

    private final Gang gang;
    private final long userID;

    private GangRole role;

    public GangMember(final Gang gang, final long userID, final GangRole role) {
        this.gang = gang;
        this.userID = userID;

        this.role = role;
    }

    /**
     * Update member role.
     *
     * @param role - Role being updated
     */
    public void setRole(final GangRole role) {
        final GangRole old = this.role;
        this.role = role;
        new SQLQuery(new DBSocialize(), "UPDATE `gang_members` SET `user_role` = ? WHERE (`gang_id`, `user_id`) = (?, ?)",
                    role.toString(), this.gang.getID(), this.userID)
                .execute();

        GangManager.INSTANCE.log(this.gang, "<@" + this.userID + "> role has been updated from " + old + " to " + role);
    }

    /**
     * Toggle the GangMembers dm on/off
     * - Allow the member to receive DMs about the gang when required
     *
     * @param entity - Entity being toggled
     * @param context - CommandContext to respond
     */
    public final void toggleDMs(final Entity entity, final CommandContext context) {
        final boolean dm = entity.getStats().fetch(new GangDMs()) == 1;

        entity.getStats().update(new GangDMs(), dm ? 0L : 1L);
        final EmbedBuilder builder = Embed.gang(context.getMember());

        if(!dm) {
            builder.appendDescription("You will now receive DMs about the Gang when required.");
        } else {
            builder.appendDescription("You will no longer receive DMs about the Gang.");
        }

        context.reply(builder.build()).queue();
    }

    /**
     * Toggle the GangMembers suffix on/off
     * - Add suffix if toggled on
     * - Remove suffix if toggled off
     *
     * @param entity - Entity being toggled
     * @param context - CommandContext to respond
     */
    public final void toggleSuffix(final Entity entity, final CommandContext context) {
        final boolean suffix = entity.getStats().fetch(new GangSuffix()) == 1;

        entity.getStats().update(new GangSuffix(), suffix ? 0L : 1L);

        if(!suffix) {
            this.updateNickname(null);
        } else {
            this.removeNickname();
        }

        context.reply(Embed.gang(context.getMember())
                .appendDescription("Your Gang Suffix has been toggled " + (!suffix ? "on" : "off") + ".")
                .build()).
                queue();
    }

    /**
     * Update Nicknames for a GangMember.
     *
     * @param old - Old suffix to be removed
     */
    public void updateNickname(final String old) {
        final Entity entity = this.getEntity();
        if(entity == null) {
            GangManager.INSTANCE.log(this.gang, "Failed to `updateNickname`, Entity == null: {UserID: " + this.userID + "}");
            return;
        }

        if(entity.getStats().fetch(new GangSuffix()) != 1) {
            return;
        }

        final Member member = entity.getMember();
        if(member == null) {
            GangManager.INSTANCE.log(this.gang, "Failed to `updateNickname`, Member == null: {UserID: " + this.userID + "}");
            return;
        }

        String nickname;
        if(old == null) {
            nickname = member.getEffectiveName();
        } else {
            nickname = member.getEffectiveName().replace(" " + old, "");
        }

        nickname += " " + UString.superscript(this.gang.getData().fetch(GangData.Type.NAME).toString());
        if(nickname.length() > 32) {
            nickname = nickname.substring(0, 31);
        }

        try {
            final String nick = nickname;
            member.modifyNickname(nickname).queue(null, ex -> {
                Titan.INSTANCE.getLogger().info("Failed for {} : {}", member.getEffectiveName(), nick, ex);
            });
        } catch (final HierarchyException ex) {
            Titan.INSTANCE.getLogger().info("[GangMember] - Failed to update {} suffix due to Hierarchy.", member.getEffectiveName());
        }
    }

    /**
     * Remove a users Nickname if they have a suffix from Gang.
     *
     * Error handling for null Entity or Member.
     */
    public void removeNickname() {
        final Entity entity = this.getEntity();
        if(entity == null) {
            GangManager.INSTANCE.log(this.gang, "Failed to `updateNickname`, Entity == null: {UserID: " + this.userID + "}");
            return;
        }

        final Member member = entity.getMember();
        if(member == null) {
            GangManager.INSTANCE.log(this.gang, "Failed to `updateNickname`, Member == null: {UserID: " + this.userID + "}");
            return;
        }

        try {
            member.modifyNickname(member.getEffectiveName().replace(" " + UString.superscript(this.gang.getData().fetch(GangData.Type.NAME).toString()), "")).queue();
        } catch (final HierarchyException ex) {
            Titan.INSTANCE.getLogger().info("[GangMember] - Failed to update {} suffix due to Hierarchy.", member.getEffectiveName());
        }
    }

    /**
     * Fetch the Entity from the Guild in a safe method
     *
     * @return - Entity object or null if Guild is invalid
     */
    public final Entity getEntity() {
        final Guild guild = Bot.getJDA().getGuildById(this.gang.getGuildID());
        if(guild == null) {
            Titan.INSTANCE.getLogger().info("[GangMember] - Invalid Guild ({}) ", this.gang.getGuildID());
            return null;
        }

        return EntityHandler.INSTANCE.getEntity(guild, this.userID);
    }

    /**
     * Return a Stat Key from the EntityStats class as Gang stats are stored there
     *
     * @param key - Key
     * @return - Return boolean based on 1 || 0
     */
    public final boolean fetch(final StatKey key) {
        final Entity entity = this.getEntity();

        return entity != null && entity.getStats().fetch(key) == 1L;
    }

    public final long getUserID() {
        return this.userID;
    }

    public final GangRole getRole() {
        return this.role;
    }
}
