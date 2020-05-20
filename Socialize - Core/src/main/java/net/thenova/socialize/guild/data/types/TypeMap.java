package net.thenova.socialize.guild.data.types;

import de.arraying.kotys.JSON;
import net.thenova.socialize.database.DBSocialize;
import net.thenova.socialize.guild.data.DataType;
import net.thenova.socialize.guild.data.types.property.Property;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TypeMap extends DataType {

    private final Map<String, Property> properties = new ConcurrentHashMap<>();

    @Override
    public void load() {
        new SQLQuery(new DBSocialize(), super.SELECT_QUERY, guildID, this.getUniqueIdentifier())
                .execute(res -> {
                    try {
                        if(res.next()) {
                            new JSON(res.getString("data_value"))
                                    .raw()
                                    .forEach((key, val) -> this.properties.put(key, new Property(val.toString())));
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[DataType] [TypeValue] - Failed to select `data_value` from SQL.", ex);
                    }
                });
    }

    public final boolean contains(String key) {
        if(key == null) {
            throw new IllegalArgumentException("key is null");
        }

        return this.properties.containsKey(key.toLowerCase());
    }

    /**
     * Gets the property by key.
     * @param key The key.
     * @return Never null property (content may be null).
     */
    public final Property get(String key) {
        if(key == null) {
            throw new IllegalArgumentException("key is null");
        }

        return properties.getOrDefault(key.toLowerCase(), new Property());
    }

    /**
     * Gets all the keys in the TypeMap.
     *
     * @return - Return Set of keys
     */
    public final Set<String> getKeys() {
        return properties.keySet();
    }

    /**
     * Sets the property to a new value.
     * @param key The key.
     * @param value The value.
     */
    public final Property set(String key, Object value) {
        if(key == null) {
            throw new IllegalArgumentException("key is null");
        }

        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }

        key = key.toLowerCase();
        final Property previous = this.properties.put(key, new Property(value.toString()));

        this.save();
        return previous;
    }

    /**
     * Unsets a property.
     *
     * @param key The key.
     * @return The previous property, may be null.
     */
    public final Property unset(String key) {
        if(key == null) {
            throw new IllegalArgumentException("key is null");
        }

        key = key.toLowerCase();
        final Property previous = properties.getOrDefault(key, new Property());
        properties.remove(key);

        this.save();
        return previous;
    }

    /**
     * Gets the contents of the list.
     * @return An immutable list of properties.
     */
    public final Map<String, Property> contents() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Saves properties back to database.
     */
    public void save() {
        final JSON json = new JSON();

        this.properties.forEach((key, property) -> json.put(key, property.toString()));
        this.save(json.marshal());
    }
}
