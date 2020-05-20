package net.thenova.socialize.guild.data.types;

import de.arraying.kotys.JSONArray;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.data.DataType;
import net.thenova.socialize.guild.data.types.property.Property;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TypeList extends DataType {

    private final List<Property> properties = new ArrayList<>();

    @Override
    public void load() {
        new SQLQuery(new DBSocialize(), super.SELECT_QUERY, guildID, this.getUniqueIdentifier())
                .execute(res -> {
                    try {
                        if(res.next()) {
                            new JSONArray(res.getString("data_value"))
                                    .raw()
                                    .forEach(val -> this.properties.add(new Property(val.toString())));
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[DataType] [TypeValue] - Failed to select `data_value` from SQL.", ex);
                    }
                });
    }

    /**
     * Adds a property.
     * @param value The value.
     * @return The newly added property.
     */
    public final Property add(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }

        final Property property = new Property(value.toString());
        properties.add(property);

        this.save();
        return property;
    }

    /**
     * Removes a property.
     * This will remove the first and only the first property with the value.
     * @param value The value.
     * @return The property, or null if it was not present.
     */
    public final Property remove(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }

        final String data = value.toString();
        final Property removed = properties.stream()
                .filter(property -> property.asString().equals(data))
                .findFirst()
                .orElse(null);
        if(removed != null) {
            properties.remove(removed);
            this.save();
        }
        return removed;
    }

    /**
     * Gets the contents of the list.
     * @return An immutable list of properties.
     */
    public final List<Property> contents() {
        return Collections.unmodifiableList(properties);
    }

    /**
     * Saves properties back to database.
     */
    private void save() {
        final JSONArray array = new JSONArray();
        this.properties.forEach(prop -> array.append(prop.toString()));

        this.save(array.marshal());
    }
}
