package net.thenova.titan.spigot.module.togglepvp.listeners;

import me.angeschossen.lands.api.player.LandPlayer;
import net.thenova.titan.spigot.data.compatability.Remain;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.togglepvp.handler.PVPHandler;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class ConnectionEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public final void onJoin(final PlayerJoinEvent event) {
        final User user = UserHandler.INSTANCE.getUser(event.getPlayer());
        final LandPlayer lp = PVPHandler.INSTANCE.getLands().getLandPlayer(user.getUUID());
        String status = (boolean) user.getModule(UserPVP.class).get(new KeyPVPDisabled()) ? "&cdisabled" : "&aenabled";
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
