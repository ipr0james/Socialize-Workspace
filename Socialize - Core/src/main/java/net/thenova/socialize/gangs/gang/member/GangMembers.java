package net.thenova.socialize.gangs.gang.member;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeMembers;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.util.response.ResponseReaction;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.util.URandom;

import java.sql.SQLException;
import java.util.*;
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
public final class GangMembers {

    private final Gang gang;
    private final Map<Long, GangMember> members = new HashMap<>(); // UserID, GangMember object
    private final Map<Long, Long> invited = new HashMap<>(); // UserID, Timestamp

    public GangMembers(final Gang gang) {
        this.gang = gang;

        new SQLQuery(new DBSocialize(), "SELECT * FROM `gang_members` WHERE `gang_id` = ?", gang.getID())
                .execute(res -> {
                    try {
                        while(res.next()) {
                            final long user = res.getLong("user_id");
                            this.members.put(user, new GangMember(gang, user, GangRole.valueOf(res.getString("user_role"))));
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[GangMembers] - Failed to load gang members for ID: {}", gang.getID());
                    }
                });
    }

    /**
     * Invite a member to join the Gang.
     *
     * @param context - CommandContext from the invite command
     * @param sender - Entity who send the invite command
     * @param member - Member being invited to the gang
     */
    public final void inviteSend(final CommandContext context, final Entity sender, final Member member) {
        final long id = member.getUser().getIdLong();

        if(this.members.containsKey(id)) {
            context.error("That member is already a part of your gang.");
            return;
        }

        if(this.invited.containsKey(id)) {
            if(this.invited.get(id) > System.currentTimeMillis()) {
                context.error("That member has already been invited to your gang.");
                return;
            } else {
                this.invited.remove(id);
            }
        }

        final Entity invited = EntityHandler.INSTANCE.getEntity(member);
        if(invited == null) {
            context.error("That member does not exist.");
            return;
        }

        if(invited.getStats().fetch(new GangID()) != -1L) {
            context.error("That member is already a part of another gang.");
            return;
        }

        if(this.members.size() >= ((GangUpgradeMembers) this.gang.getUpgrades().fetch(GangUpgradeType.MEMBERS)).getMaxMembers()) {
            context.error("Your gang is currently at maximum members.");
            return;
        }

        context.reply("<@" + invited.getUserID() + ">");
        context.reply(Embed.gang(member, "You have been invited to join `" + this.gang.getData().fetch(GangData.Type.NAME) + "` by <@" + sender.getUserID() + ">"))
                .queue(message -> {
                    ResponseReaction.create(message, member, false)
                            .cancellable(60, () -> {
                                message.editMessage(Embed.gang(context.getMember())
                                        .setTitle("Action Cancelled!")
                                        .appendDescription("Gang invitation has expired.")
                                        .build())
                                        .queue();
                            })
                            .reaction(Bot.INSTANCE.getEmoji("gang_tick"),
                                    click -> { this.inviteAccept(message, member); })
                            .reaction(Bot.INSTANCE.getEmoji("gang_cross"),
                                    click -> {
                                        this.invited.remove(member.getUser().getIdLong());

                                        ResponseReaction.remove(message.getIdLong());
                                        message.editMessage(Embed.gang(member, "<@" + member.getUser().getIdLong() + "> has denied the gang invite.")).queue();
                                    });
                });
    }

    /**
     * Action when a member accepts a gang invite
     *
     * @param message - Message reacted to, to accept
     * @param member - Member accepting
     */
    private void inviteAccept(final Message message, final Member member) {
        final long id = member.getUser().getIdLong();
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);
        assert entity != null;

        if(entity.getStats().fetch(new GangID()) != -1L) {
            ResponseReaction.remove(message.getIdLong());
            message.editMessage(Embed.error(member, "You are already part of a gang.")).queue();
            return;
        }

        if(!GangManager.INSTANCE.isGang(this.gang.getID())) {
            ResponseReaction.remove(message.getIdLong());
            message.editMessage(Embed.error(member, "That gang no longer exists.")).queue();
            return;
        }

        if(this.members.size() >= ((GangUpgradeMembers) this.gang.getUpgrades().fetch(GangUpgradeType.MEMBERS)).getMaxMembers()) {
            ResponseReaction.remove(message.getIdLong());
            message.editMessage(Embed.error(member, "Your gang is currently at maximum members.")).queue();
            return;
        }

        ResponseReaction.remove(message.getIdLong());
        this.invited.remove(id);
        this.add(entity, GangRole.MEMBER);

        message.editMessage(Embed.gang(member, entity.asMention() + " has now joined " + this.gang.getData().fetch(GangData.Type.NAME))).queue();
    }

    public void inviteJoin(final CommandContext context, final Entity entity) {
        if((boolean)this.gang.getData().fetch(GangData.Type.OPEN_STATUS)) {
             if(this.members.size() >= ((GangUpgradeMembers) this.gang.getUpgrades().fetch(GangUpgradeType.MEMBERS)).getMaxMembers()) {
                 context.error("This gang is already at maximum members.");
                 return;
             }

             this.add(entity, GangRole.MEMBER);
             context.reply(Embed.gang(context.getMember(), "You are now a part of the gang `" + gang.getData().fetch(GangData.Type.NAME) + "`")).queue();
        } else {
            context.error("This gang is currently closed and requires an invitation to join.");
        }
    }

    /**
     * Add a member to the gang with the specified role
     *
     * @param entity - Entity being added
     * @param role - Role being set
     * @return - Return the GangMember object
     */
    public final GangMember add(final Entity entity, final GangRole role) {
        final GangMember member = new GangMember(this.gang, entity.getUserID(), role);

        new SQLQuery(new DBSocialize(), "INSERT INTO `gang_members` (`gang_id`, `user_id`, `user_role`) VALUES (?, ?, ?)",
                    this.gang.getID(), entity.getUserID(), role.toString())
                .execute();
        entity.getStats().update(new GangID(), this.gang.getID());
        this.members.put(entity.getUserID(), member);

        entity.getMultiplier().check();

        return member;
    }

    /**
     * Handle promotion of Members to Officers and transfer of leadership to another Officer.
     *
     * @param context - CommandContext from command executed
     * @param sender - Leader
     * @param entity - Entity being promoted
     */
    public final void promote(final CommandContext context, final Entity sender, final Entity entity) {
        if(!this.members.containsKey(entity.getUserID())) {
            context.error(entity.asMention() + " is not a part of your gang.");
            return;
        }

        final GangMember member = this.fetch(entity.getUserID());
        if(member.getRole() == GangRole.MEMBER) {
            member.setRole(GangRole.OFFICER);
            context.reply(Embed.gang(context.getMember(), entity.asMention() + " has been promoted to Officer.")).queue();
        } else {
            context.reply(Embed.gang(context.getMember(), "Are you sure you would like to give leadership of your gang to " + entity.asMention() + "?" +
                    "\nThis cannot be undone.")).queue(message -> {
                ResponseReaction.create(message, context.getMember(), false)
                        .reaction(Bot.INSTANCE.getEmoji("gang_tick"), click -> {
                            if(gang.getMembers().fetch(sender.getUserID()).getRole() != GangRole.LEADER) {
                                message.editMessage(Embed.error(context.getMember(), "You are not the Gang Leader.")).queue();
                            } else {
                                if(!this.members.containsKey(entity.getUserID())) {
                                    message.editMessage(Embed.error(context.getMember(), entity.asMention() + " is not a part of your gang.")).queue();
                                    return;
                                }

                                final GangMember current = this.fetch(sender.getUserID());
                                current.setRole(GangRole.OFFICER);
                                member.setRole(GangRole.LEADER);

                                context.reply(entity.asMention());
                                context.reply(Embed.gang(Objects.requireNonNull(entity.getMember()))
                                        .appendDescription(entity.asMention() + " you are now the leader of **" + gang.getData().fetch(GangData.Type.NAME) + "**"))
                                        .queue();
                            }
                        })
                        .reaction(Bot.INSTANCE.getEmoji("gang_cross"), click -> {
                            ResponseReaction.remove(message.getIdLong());
                            message.editMessage(Embed.gang(context.getMember())
                                    .setTitle("Action Cancelled!")
                                    .appendDescription("You have cancelled passing Leadership.")
                                    .build())
                                    .queue();
                        })
                        .cancellable(30, () -> message.editMessage(Embed.error(context.getMember(), "Action has timed out.")).queue());
            });
        }
    }

    /**
     * Demote a Member from Officer to Member
     *
     * @param context - CommandContext from command executed
     * @param sender - Leader
     * @param entity - Entity being demoted
     */
    public final void demote(final CommandContext context,  final Entity sender, final Entity entity) {
        if(!this.members.containsKey(entity.getUserID())) {
            context.error(entity.asMention() + " is not a part of your gang.");
            return;
        }

        if(sender.getUserID() == entity.getUserID()) {
            context.error("You cannot demote yourself.");
            return;
        }

        final GangMember member;
        if((member = this.fetch(entity.getUserID())).getRole() == GangRole.OFFICER) {
            member.setRole(GangRole.MEMBER);
            context.reply(Embed.gang(context.getMember(), entity.asMention() + " has been demoted from Officer to Member.")).queue();
        } else {
            context.error(entity.asMention() + " is not an Officer and cannot be demoted.");
        }
    }

    /**
     * Method for when the member leaves the gang by command.
     *
     * @param context - Command executed
     * @param entity - Entity leaving
     */
    public final void leave(final CommandContext context, final Entity entity) {
        if(this.members.get(entity.getUserID()).getRole() == GangRole.LEADER) {
            context.error("As leader, you must transfer gang ownership or disband to leave.");
            return;
        }

        this.handleLeave(entity);
        context.reply(Embed.gang(context.getMember(), "You have left the gang.")).queue();
    }

    /**
     * Handle guild actions when a gang member leaves the Guild.
     *
     * @param entity - Entity leaving
     */
    public final void leaveGuild(final Entity entity) {
        final GangMember member = this.fetch(entity.getUserID());
        if(member.getRole() != GangRole.LEADER) {
            this.handleLeave(entity);
            return;
        }

        if(this.members.size() == 1) {
            GangManager.INSTANCE.disband(this.gang);
            return;
        }

        List<GangMember> members;
        if((members = this.getMembers(GangRole.OFFICER)).isEmpty()) {
            members = this.getMembers(GangRole.MEMBER);
        }

        final GangMember leader = members.get(URandom.r(0, members.size()));
        leader.setRole(GangRole.LEADER);

        final TextChannel channel = GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GuildChannelsData()).getChannel(GuildChannelsData.Type.GANG);
        if(channel != null) {
            final Entity newLeader = leader.getEntity();
            assert newLeader != null;

            channel.sendMessage(newLeader.asMention()).queue();
            channel.sendMessage(Embed.gang(newLeader.getMember(), "You have been promoted to leader of **" + this.gang.getData().fetch(GangData.Type.NAME) + "** as the previous leader left the server.")).queue();
        }
    }

    /**
     * Handle when a member is kicked from the gang
     *
     * @param context - Command executed
     * @param kicker - Officer/Leader kicking the member
     * @param entity - Member/Officer who was kicked from the Gang
     */
    public final void kick(final CommandContext context, final Entity kicker, final Entity entity) {
        if(!this.members.containsKey(entity.getUserID())) {
            context.error(entity.asMention() + " is not a part of your gang.");
            return;
        }

        final GangRole kickedRole = this.members.get(entity.getUserID()).getRole();
        final GangRole kickerRole = this.members.get(kicker.getUserID()).getRole();

        if(kickedRole == GangRole.LEADER) {
            context.error("You cannot kick the Gang Leader, nice try though.");
            return;
        }

        if(kickedRole == GangRole.OFFICER && kickerRole != GangRole.LEADER) {
            context.error("Only the Gang Leader is able to kick Officers.");
            return;
        }

        this.handleLeave(entity);
        context.reply(Embed.gang(context.getMember(), entity.asMention() + " has been kicked from the gang.")).queue();
    }

    /**
     * Internal workings for removing the member from the Gang.
     *
     * @param entity - Entity being removed
     */
    private void handleLeave(final Entity entity) {
        entity.getStats().update(new GangID(), -1L);

        this.members.remove(entity.getUserID());
        new SQLQuery(new DBSocialize(), "DELETE FROM `gang_members` WHERE (`gang_id`, `user_id`) = (?, ?)",
                this.gang.getID(), entity.getUserID())
                .execute();
    }

    /**
     * Fetch the GangMember object for a specific user
     *
     * @param userID - ID of the user being fetched
     * @return - Return the GangMember object of the user
     */
    public final GangMember fetch(final long userID) {
        return this.members.get(userID);
    }

    public final Collection<GangMember> getMembers() {
        return this.members.values();
    }
    public final List<GangMember> getMembers(final GangRole role) {
        return this.members.values().stream().filter(member -> member.getRole() == role).collect(Collectors.toList());
    }
}
