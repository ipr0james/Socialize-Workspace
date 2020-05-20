package net.thenova.titan.spigot.module.togglepvp.user;

import me.angeschossen.lands.api.player.LandPlayer;
import net.thenova.titan.spigot.data.compatability.Remain;
import net.thenova.titan.spigot.data.database.DatabaseCore;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.togglepvp.handler.PVPHandler;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.user.module.data.DataKey;
import net.thenova.titan.spigot.users.user.module.data.UserDataModule;

import java.util.Collections;
import java.util.List;

public final class UserPVP extends UserDataModule {

    public UserPVP() {
        super(new DatabaseCore(), "user_pvp_data");
    }

    @Override
    public List<DataKey> keys() {
        return Collections.singletonList(new KeyPVPDisabled());
    }

    @Override
    public void load() {

    }

    @Override
    public void update(final DataKey key, Object value) {
        super.update(key, value);

        final LandPlayer lp = PVPHandler.INSTANCE.getLands().getLandPlayer(this.user.getUUID());
        String status = (boolean) this.user.getModule(UserPVP.class).get(new KeyPVPDisabled()) ? "&cdisabled" : "&aenabled";
        if(lp != null && lp.isInWar()) {
            status = "&aenabled";
        }

        Remain.sendActionBar(user.getPlayer(),
                MessageHandler.INSTANCE.build("module.togglepvp.actionbar")
                        .placeholder(
                                new Placeholder("status", status)
                        )
                        .getMessage(user.getPlayer()));
    }
}
