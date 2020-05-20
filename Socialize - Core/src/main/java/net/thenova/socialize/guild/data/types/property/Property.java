package net.thenova.socialize.guild.data.types.property;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public final class Property {

    private final String value;

    /**
     * Creates a new property.
     */
    public Property() {
        this(null);
    }

    /**
     * Creates a new property.
     *
     * @param value The value of the property.
     */
    public Property(String value) {
        this.value = value;
    }

    /**
     * Defines that the property should default to something.
     *
     * @param fallback The default value.
     * @return The property that will definitely contain a value.
     */
    public Property defaulting(Object fallback) {
        return value != null ? this : new Property(fallback.toString());
    }

    /**
     * Gets the value as a boolean.
     *
     * @return A boolean.
     */
    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets the value as a byte.
     *
     * @return A byte.
     */
    public byte asByte() {
        return Byte.parseByte(value);
    }

    /**
     * Gets the value as a character.
     *
     * @return A character.
     */
    public char asChar() {
        return value.length() == 0 ? 0 : value.charAt(0);
    }

    /**
     * Gets the value as a short.
     *
     * @return A short.
     */
    public short asShort() {
        return Short.parseShort(value);
    }

    /**
     * Gets the value as an integer.
     *
     * @return An integer.
     */
    public int asInt() {
        return Integer.parseInt(value);
    }

    /**
     * Gets the value as a long.
     *
     * @return A long.
     */
    public long asLong() {
        return Long.parseLong(value);
    }

    /**
     * Gets the value as a float decimal.
     *
     * @return A float.
     */
    public float asFloat() {
        return Float.parseFloat(value);
    }

    /**
     * Gets the value as a double decimal.
     *
     * @return A double.
     */
    public double asDouble() {
        return Double.parseDouble(value);
    }

    /**
     * Gets the value as a string.
     *
     * @return A string.
     */
    public String asString() {
        return value;
    }

    /**
     * Gets the value as a JSON object
     * 
     * @return A JSON object.
     */
    public final JSON asJSON() {
        return new JSON(this.value);
    }

    public final JSONArray asArray() {
        return new JSONArray(this.value);
    }

    public final Set<Object> asSet() {
        final JSONArray array = new JSONArray(this.value);
        final Set<Object> rtn = new HashSet<>();

        for(int i = 0; i < array.length(); i++) {
            rtn.add(array.object(i));
        }

        return rtn;
    }

    /**
     * Gets the object as a string.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return this.asString();
    }
}
