package net.thenova.socialize.level;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.guild.data.entries.GuildRoleData;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public final class TaskMOTW implements Runnable {

    @Override
    public void run() {
        GuildHandler.INSTANCE.getGuilds().forEach(this::handle);
        new SQLQuery(new DBSocialize(), "UPDATE `entity_stats` SET `stat_value` = 0 WHERE `stat_key` = 'xp_week'").execute();
    }

    /**
     * Handle tasks for singular guilds
     * - Select and sort all entities for highest Weekly XP gain
     * - Check that there is members, the guild is valid and the member is also valid
     * - Check that the role has been configured
     * - Remove the role from the previous or any current MOTW
     * - Add the role to the new MOTW
     * - Check the text channel #longue is configured to send the message to tell the member
     * - Send configured message or error message
     *
     * @param guildID - Guild being handled.
     */
    private void handle(final long guildID) {
        final List<Entry> values = new ArrayList<>();
        new SQLQuery(new DBSocialize(), "SELECT `entity_data`.`user_id`, `entity_stats`.`stat_value` FROM `entity_data` INNER JOIN `entity_stats` ON `entity_data`.`entity_id`=`entity_stats`.`entity_id` WHERE (`entity_data`.`guild_id`, `entity_stats`.`stat_key`) = (?, ?)", guildID, "xp_week")
                .execute(res -> {
                    try {
                        while (res.next()) {
                            values.add(new Entry(res.getLong("entity_data.user_id"), res.getLong("entity_stats.stat_value")));
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[TaskMOTW] - Failed to load entity data for selection.");
                    }
                });
        values.sort((o1, o2) -> o2.value.compareTo(o1.value));

        final Guild guild;
        final Member member;
        if(values.isEmpty()
                || (guild = Bot.getJDA().getGuildById(guildID)) == null
                || (member = guild.getMemberById(values.get(0).userID)) == null) {
            return;
        }

        final Role role = GuildHandler.INSTANCE.fetch(guildID, new GuildRoleData()).getRole(GuildRoleData.Type.MEMBER_OF_THE_WEEK);
        if(role == null) {
            GuildHandler.INSTANCE.messageError(guildID, "[MemberOfTheWeek] - Role was null and could not be added.");
            return;
        }

        guild.getMembers().stream()
                .filter(mem -> mem.getRoles().stream().anyMatch(r -> r.getIdLong() == role.getIdLong()))
                .forEach(mem -> guild.removeRoleFromMember(mem, role).queue());

        guild.addRoleToMember(member, role).queue();

        final TextChannel channel;
        if((channel = GuildHandler.INSTANCE.fetch(guildID, new GuildChannelsData()).getChannel(GuildChannelsData.Type.LOUNGE)) == null) {
            GuildHandler.INSTANCE.messageError(guildID, "[MemberOfTheWeek] - Channel 'lounge' was invalid in some way, " +
                    "The member of the week was '" + member.getIdLong()
                    + "' and has received the role but the message could not be sent.");
        } else {
            channel.sendMessageFormat("<@" + member.getIdLong() + ">").queue();
            channel.sendMessage(Embed.socialize("<@" + member.getIdLong() + "> you are now the member of the week!").build()).queue();
        }
    }

    /**
     * Data entry used for sorting
     */
    @AllArgsConstructor
    private static final class Entry {
        private final long userID;
        private final Long value;
    }
}
