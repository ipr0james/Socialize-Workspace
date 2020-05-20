package net.thenova.socialize.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.games.lottery.TaskLottery;
import net.thenova.socialize.gangs.TaskGangUpkeep;
import net.thenova.socialize.guild.data.DataType;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.leaderboard.TaskLeaderboardRefresh;
import net.thenova.socialize.util.task.TaskHandler;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public enum GuildHandler {
    INSTANCE;

    private final List<Long> guilds = new ArrayList<>();

    private final Map<Long, List<DataType>> data = new HashMap<>();

    public static final String CONFIGURATION_TABLE = "guild_configuration";

    public void load() {
        new SQLQuery(new DBSocialize(), "SELECT `guild_id` FROM `guild_data`").execute(res -> {
            try {
                while(res.next()) {
                    final long id = res.getLong("guild_id");

                    this.guilds.add(id);
                    this.data.put(id, new ArrayList<>());
                }
            } catch (final SQLException ex) {
                Titan.INSTANCE.getLogger().info("[GuildHandler] - Failed to select guilds from guild table.", ex);
            }
        });
    }

    public void start() {
        this.guilds.forEach(this::start);
    }

    public void start(long guildID) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);

        if(!cal.getTime().after(currentCalendar.getTime())) {
            cal.add(Calendar.DATE, 1);
        }

        final long timeInMillis = cal.getTimeInMillis() - System.currentTimeMillis();
        GuildHandler.INSTANCE.getGuilds().forEach(guild -> {
            TaskHandler.INSTANCE.scheduleRepeating(guild, new TaskGangUpkeep(guild), timeInMillis, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
            TaskHandler.INSTANCE.scheduleRepeating(guild, new TaskLottery(guild), timeInMillis, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);

            TaskHandler.INSTANCE.scheduleRepeating(guild, new TaskLeaderboardRefresh(guild), 0L, 5, TimeUnit.MINUTES);
        });

    }

    /**
     * Fetch the given Guild's configuration settings.
     *
     * @param guildID - Guild ID
     * @param t - DataType to be cast to
     * @param <T> - DataType object to be returned after loading
     * @return - Return DataType object instance
     */
    public <T extends DataType> T fetch(final long guildID, final T type) {
        if(type == null) {
            throw new IllegalArgumentException("data type is null");
        }

        if(!this.guilds.contains(guildID)) {
            new SQLQuery(new DBSocialize(),"INSERT IGNORE INTO `guild_data` (`guild_id`) VALUES (?)", guildID).execute();
            this.guilds.add(guildID);
            this.data.put(guildID, new ArrayList<>());
        }

        final List<DataType> data = this.data.get(guildID);
        final DataType rtn = data.stream().filter(dt -> dt.getClass().equals(type.getClass())).findFirst().orElse(null);
        if(rtn != null) {
            //noinspection unchecked
            return (T) rtn;
        } else {
            type.load(guildID);
        }

        return type;
    }

    /**
     * Send an error message to the configured channel.
     *
     * @param guildID - Guild ID
     * @param message - Message to be sent
     */
    public void messageError(final long guildID, final String message) {
        final TextChannel channel = GuildHandler.INSTANCE.fetch(guildID, new GuildChannelsData()).getChannel(GuildChannelsData.Type.ERROR);
        if(channel == null) {
            return;
        }

        channel.sendMessage(Embed.socialize(message).build()).queue();
    }

    /**
     * Check whether a channel exists within a specific guild.
     *
     * @param guildID - Guild being checked
     * @param channelID - Channel ID
     * @return - Return if the channel != null
     */
    public boolean isChannel(final long guildID, final long channelID) {
        final Guild guild = Bot.getJDA().getGuildById(guildID);

        return guild != null && guild.getGuildChannelById(channelID) != null;
    }

    public List<Long> getGuilds() {
        return this.guilds;
    }
}
