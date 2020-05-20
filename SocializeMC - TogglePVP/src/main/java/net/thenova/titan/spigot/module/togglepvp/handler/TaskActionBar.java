package net.thenova.titan.spigot.module.togglepvp.handler;

import me.angeschossen.lands.api.player.LandPlayer;
import net.thenova.titan.spigot.data.compatability.Remain;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.UserHandler;

public final class TaskActionBar implements Runnable {

    @Override
    public void run() {
        UserHandler.INSTANCE.getOnlineUsers().forEach(user -> {
            final LandPlayer lp = PVPHandler.INSTANCE.getLands().getLandPlayer(user.getUUID());
            String status = (boolean) user.getModule(UserPVP.class).get(new KeyPVPDisabled()) ? "&cdisabled" : "&aenabled";
            if(lp != null && lp.isInWar()) {
                status = "&aenabled";
            }

            Remain.sendActionBar(user.getPlayer(),
                    MessageHandler.INSTANCE.build("module.togglepvp.actionbar").placeholder(
                            new Placeholder("status", status)
                    )
                    .getMessage(user.getPlayer()));
        });
    }
}
