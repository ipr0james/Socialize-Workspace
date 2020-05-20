package net.thenova.socialize.guild.data.types;

import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.data.DataType;
import net.thenova.socialize.guild.data.types.property.Property;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;

@SuppressWarnings("unused")
public abstract class TypeValue extends DataType {

    protected Property property;

    @Override
    public void load() {
        new SQLQuery(new DBSocialize(), super.SELECT_QUERY, guildID, this.getUniqueIdentifier())
                .execute(res -> {
                    try {
                        if(res.next()) {
                            this.property = new Property(res.getString("data_value"));
                        } else {
                            this.property = new Property();
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[DataType] [TypeValue] - Failed to select `data_value` from SQL.", ex);
                    }
                });
    }

    /**
     * Gets the underlying value.
     *
     * @return The underlying value.
     */
    public Property get() {
        return this.property;
    }

    /**
     * Sets the underlying value.
     *
     * @param value The new value.
     * @return The new value.
     */
    public final Property set(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }

        this.save(value.toString());
        return property = new Property(value.toString());
    }
}
