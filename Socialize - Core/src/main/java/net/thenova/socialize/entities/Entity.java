package net.thenova.socialize.entities;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.modules.*;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;

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
@Getter
public final class Entity {

    /* When data for the user was last accessed, used for caching */
    @Setter private long lastFetch;

    private long iD;

    private final long userID;
    private final long guildID;

    /* Entity Modules */
    private final EntityValidation validation;

    private final EntityStats stats;

    private final EntityBalance balance;
    private final EntityLevel level;
    private final EntityMultiplier multiplier;

    public Entity(final Member member) {
        this.lastFetch = System.currentTimeMillis();

        this.guildID = member.getGuild().getIdLong();
        this.userID = member.getUser().getIdLong();

        this.insert();

        this.validation = new EntityValidation();

        this.stats = new EntityStats(this);

        this.balance = new EntityBalance(this);
        this.level = new EntityLevel(this);
        this.multiplier = new EntityMultiplier(this);
    }

    /**
     * Check if the entity is inside of the EntityData table
     * - Insert if missing then reselect,
     * - Select ID and update value
     */
    private void insert() {
        new SQLQuery(new DBSocialize(), "SELECT `entity_id` FROM `entity_data` WHERE (`guild_id`, `user_id`) = (?, ?)", this.guildID, this.userID)
                .execute(res -> {
                    try {
                        if(!res.next()) {
                            new SQLQuery(new DBSocialize(), "INSERT INTO `entity_data` (`guild_id`, `user_id`) VALUES (?, ?)", this.guildID, this.userID)
                                    .execute(this::insert);
                        } else {
                            this.iD = res.getLong("entity_id");
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[Entity] - Failed to select EntityID from EntityData for user {}", this.userID, ex);
                    }
                });
    }

    public void leave() {
        final long gangID = this.stats.fetch(new GangID());
        if(gangID != -1) {
            GangManager.INSTANCE.getGang(this.guildID, gangID).getMembers().leaveGuild(this);
        }
    }

    public final Member getMember() {
        final Guild guild = Bot.getJDA().getGuildById(this.guildID);
        if(guild == null) {
            Titan.INSTANCE.getLogger().info("[Entity] - Invalid Guild ({}) ", this.guildID);
            return null;
        }

        return guild.getMemberById(this.userID);
    }

    public final String asMention() {
        return "<@" + this.userID + ">";
    }

    /**
     TODO:
     - Redesign:
       - Move to MemberListener
       - Move code for leaving gang to gang.leave and add automatic handling there to keep gangs self contained

    public void leave() {
        final Integer gangID;
        if((gangID = this.data.get(new GangID(), Integer.class)) != null) {
            final Gang gang = GangManager.INSTANCE.getGangById(gangID);
            final GangMembers members = gang.getMembers();

            if(members.isLeader(this.userID)) {
                if(members.getOfficers().isEmpty()
                        && members.getMembers().isEmpty()) {
                    gang.disband();
                    return;
                }

                List<GangMember> plebs;
                if(!members.getOfficers().isEmpty()) {
                    plebs = members.getOfficers();
                } else {
                    plebs = members.getPlebs();
                }

                if(plebs.size() < 1) {
                    gang.disband();
                    return;
                }

                GangMember leader = plebs.get(URandom.r(0, plebs.size()));
                leader.setRole(GangRole.LEADER);

                final TextChannel channel;
                try {
                    channel = GuildHandler.INSTANCE.getSCGuild(guildID).getData().getTextChannel("gangs");
                } catch (GuildConfigException ex) {
                    Bot.INSTANCE.getLogger().info("Gang channel was not defined: {{}}", ex.getMessage(), ex);
                    return;
                }
                final EmbedBuilder builder = Embed.gang(leader.getEntity().getMember());

                builder.appendDescription("You have been promoted to the leader of **" + gang.getName() + "** as the previous leader has left.");

                channel.sendMessage("<@" + leader.getUserID() + ">").queue();
                channel.sendMessage(builder.build()).queue();
            }

            gang.getMembers().leave(this);
        }
    }*/
}
