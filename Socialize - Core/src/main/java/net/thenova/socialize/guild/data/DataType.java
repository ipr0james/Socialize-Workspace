package net.thenova.socialize.guild.data;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;

public abstract class DataType {

    protected final String SELECT_QUERY = "SELECT `data_value` FROM `guild_configuration` WHERE (`guild_id`, `data_key`) = (?, ?)";

    protected long guildID;

    /**
     * Load the Type data
     *
     * @param guildID - GuildID of data to be loaded.
     */
    public final void load(final long guildID) {
        this.guildID = guildID;

        this.load();
    }

    public abstract void load();

    /**
     * Gets the unique identifier.
     *
     * @return A unique identifier.
     */
    protected abstract String getUniqueIdentifier();

    /**
     * Save a 'value' back to database. Automatically inset key if not present
     *
     * @param value - Value being updated.
     */
    protected final void save(final String value) {
        new SQLQuery(new DBSocialize(), this.SELECT_QUERY, this.guildID, this.getUniqueIdentifier())
                .execute(res -> {
            try {
                if(res.next()) {
                    new SQLQuery(new DBSocialize(), "UPDATE `" + GuildHandler.CONFIGURATION_TABLE + "` SET `data_value` = ? WHERE (`guild_id`, `data_key`) = (?, ?)",
                            value, this.guildID, this.getUniqueIdentifier()).execute();
                } else {
                    new SQLQuery(new DBSocialize(), "INSERT INTO `" + GuildHandler.CONFIGURATION_TABLE + "` (`guild_id`, `data_key`, `data_value`) VALUES (?, ?, ?)",
                                this.guildID, this.getUniqueIdentifier(), value)
                            .execute();
                }
            } catch (final SQLException ex) {
                Titan.INSTANCE.getLogger().info("[DataType] - Failed to retrieve information for a DataType for Guild data", ex);
            }
        });
    }
}
