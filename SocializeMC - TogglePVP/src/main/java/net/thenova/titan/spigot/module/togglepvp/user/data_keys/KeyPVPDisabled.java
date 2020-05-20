package net.thenova.titan.spigot.module.togglepvp.user.data_keys;

import net.thenova.titan.spigot.users.user.module.data.DataKey;

public final class KeyPVPDisabled implements DataKey {
    @Override
    public String key() {
        return "pvp_disabled";
    }

    @Override
    public Object value() {
        return true;
    }

    @Override
    public Object cast(final String value) {
        return value.equals("1") || (!value.equals("0") && Boolean.parseBoolean(value));
    }
}
