package net.thenova.socialize.entities.modules.stat_keys.coins;

import net.thenova.socialize.entities.modules.stat_keys.StatKey;

public final class CoinsCasinoLoss implements StatKey {

    @Override
    public String key() {
        return "coins_casino_loss";
    }

    @Override
    public Long value() {
        return 0L;
    }
}
