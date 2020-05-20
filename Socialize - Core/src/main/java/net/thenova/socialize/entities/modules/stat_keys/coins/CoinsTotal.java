package net.thenova.socialize.entities.modules.stat_keys.coins;

import net.thenova.socialize.entities.modules.stat_keys.StatKey;

public final class CoinsTotal implements StatKey {

    @Override
    public String key() {
        return "coins_total";
    }

    @Override
    public Long value() {
        return 0L;
    }
}
