package net.thenova.socialize.entities.modules;

import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCasinoLoss;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCurrent;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsTotal;

public final class EntityBalance {

    public enum Reason {
        ADMIN,
        SYSTEM,
        CASINO
    }

    private final EntityStats stats;

    public EntityBalance(final Entity entity) {
        this.stats = entity.getStats();
    }

    public void add(final long value, final Reason reason) {
        this.stats.increment(new CoinsCurrent(), value);
        this.stats.increment(new CoinsTotal(), value);
    }

    public void take(final long value, final Reason reason) {
        this.stats.increment(new CoinsCurrent(), -value);

        switch(reason) {
            case CASINO:
                this.stats.increment(new CoinsCasinoLoss(), value);
            default:
                break;
        }
    }

    public void set(final long value, final Reason reason) {
        this.stats.update(new CoinsCurrent(), value);
    }

    public long fetch() {
        return this.stats.fetch(new CoinsCurrent());
    }
}
