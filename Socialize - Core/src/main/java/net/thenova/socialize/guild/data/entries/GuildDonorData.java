package net.thenova.socialize.guild.data.entries;

import net.thenova.socialize.guild.data.types.TypeMap;

public class GuildDonorData extends TypeMap {

    public enum Type {
        DONOR_MULTIPLIER_COINS("donor_multiplier_coins", 0.5),
        DONOR_MULTIPLIER_EXPERIENCE("donor_multiplier_experience", 0.5);

        private final String name;
        private final double def;

        Type(final String name, final double def) {
            this.name = name;
            this.def = def;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * Fetch a channel by a specific CHANNEL_ type
     *
     * @param type - Channel being fetched
     * @return - Return the channel by ID or null based on conditions
     */
    public double fetch(final Type type) {
        return super.get(type.toString()).defaulting(type.def).asDouble();
    }

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     */
    @Override
    protected final String getUniqueIdentifier() {
        return "guild_donor";
    }
}

