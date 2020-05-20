package net.thenova.socialize.gangs;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.gang.GangConfigData;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public enum GangManager {
    INSTANCE;

    private final static Set<String> BLOCKED_WORDS = Set.of("@", "#", ":", "```", "discordtag", "everyone", "here", "temp");

    @Getter private final Map<Long, Map<Long, Gang>> gangs = new HashMap<>(); // GuildID, Map<Gang ID, Gang object>

    /**
     * TODO
     * - Implement gang functionality within Manager/Gang itself
     * - GangCommand extends Command etc. Gang role requirement for permissions handling
     * -
     */

    public void load() {
        new SQLQuery(new DBSocialize(), "SELECT * FROM `gang_gangs`")
                .execute(res -> {
                    try {
                        while (res.next()) {
                            final long gangID = res.getLong("gang_id");
                            final long guildID = res.getLong("guild_id");

                            if(!this.gangs.containsKey(guildID)) {
                                this.gangs.put(guildID, new HashMap<>());
                            }

                            this.gangs.get(guildID).put(gangID, new Gang(gangID, guildID));
                        }
                    } catch (SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[GangManager] - Gang loading has thrown an exception", ex);
                    }
                });
    }

    public synchronized void createGang(final Entity creator, final CommandContext context) {
        final long currentGangID = creator.getStats().fetch(new GangID());
        if(currentGangID != -1L) {
            if(GangManager.INSTANCE.getGang(creator.getGuildID(), currentGangID)
                    .getMembers()
                    .fetch(creator.getUserID()).getRole() == GangRole.LEADER) {
                context.error("You already own a gang.");
            } else {
                context.error("You are already part of a gang. You must leave first.");
            }
            return;
        }

        final GangConfigData data = GuildHandler.INSTANCE.fetch(creator.getGuildID(), new GangConfigData());
        final long cost = data.get(GangConfigData.Type.CREATION_PRICE).asLong();
        if(creator.getBalance().fetch() < cost) {
            context.reply(Embed.error(context.getMember()).addField("You need " + cost + Bot.INSTANCE.getEmoji("coins") + " to start a gang.",
                    EmbedBuilder.ZERO_WIDTH_SPACE + "\n"
                            + "Balance **-** " + creator.getBalance().fetch() + Bot.INSTANCE.getEmoji("coins")
                            + "\n" + EmbedBuilder.ZERO_WIDTH_SPACE,
                    false).build()).queue();
            return;
        }

        final String name = String.join(" ", context.getArguments());
        final long max = data.get(GangConfigData.Type.CREATION_NAME_LENGTH_MAX).asLong();
        if(name.length() > max) {
            context.error("Your gangs name cannot be longer than " + max + " characters.");
            return;
        }

        final long min = data.get(GangConfigData.Type.CREATION_NAME_LENGTH_MIN).asLong();
        if(name.isEmpty() || name.length() < min) {
            context.error("Your gangs name must be " + min + " character" + (min == 1 ? "": "s") + " or more.");
            return;
        }

        final String word;
        if((word = GangManager.BLOCKED_WORDS.stream().filter(name::contains).findFirst().orElse(null)) != null) {
            context.error("Gang name cannot contain `" + word + "`.");
            return;
        }

        if(GangManager.INSTANCE.isGang(creator.getGuildID(), name)) {
            context.error("There is already a gang using that name.");
            return;
        }

        final long guildID = context.getGuild().getIdLong();
        new SQLQuery(new DBSocialize(), "INSERT INTO `gang_gangs` (`guild_id`) VALUES (?)", guildID).execute();
        new SQLQuery(new DBSocialize(), "SELECT last_insert_id() AS `last_id` FROM `gang_gangs`")
                .execute(res -> {
                    try {
                        if(res.next()) {
                            final long gangID = res.getLong("last_id");
                            final Gang gang = new Gang(gangID, guildID);

                            if(!this.gangs.containsKey(guildID)) {
                                this.gangs.put(guildID, new HashMap<>());
                            }

                            this.gangs.get(guildID).put(gangID, gang);

                            gang.getMembers().add(creator, GangRole.LEADER);
                            gang.getData().set(GangData.Type.NAME, name);
                            creator.getBalance().take(cost, EntityBalance.Reason.SYSTEM);

                            context.reply(Embed.gang(context.getMember())
                                        .appendDescription("Your gang has been created.\n" + Embed.Z)
                                        .setFooter("Use `" + CommandMap.INSTANCE.getPrefix(guildID) + "gang info` to view information about your gang.", null)
                                        .build())
                                    .queue();
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[GangManager] - Failed to select last_insert_id when {} created a gang.", creator.getUserID(), ex);
                    }
                });
    }

    public final void disband(final Gang gang) {
        gang.disband();
        this.gangs.remove(gang.getGuildID()).remove(gang.getID());
    }

    public final Gang getGang(long guildID, final long gangID) {
        return this.gangs.get(guildID).get(gangID);
    }

    public boolean isGang(final long gangID) {
        return this.gangs.values().stream().anyMatch(gangs -> gangs.keySet().stream().anyMatch(id -> gangID == id));
    }
    public boolean isGang(final long guildID, final String name) {
        return this.gangs.getOrDefault(guildID, new HashMap<>()).values()
                .stream()
                .anyMatch(gang -> ((String) gang.getData().fetch(GangData.Type.NAME)).equalsIgnoreCase(name));
    }

    public void log(Gang gang, String message) {

    }

    public Gang getGangByName(long guildID, String name) {
        return this.gangs.get(guildID).values().stream()
                .filter(gang -> ((String) gang.getData().fetch(GangData.Type.NAME)).equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
