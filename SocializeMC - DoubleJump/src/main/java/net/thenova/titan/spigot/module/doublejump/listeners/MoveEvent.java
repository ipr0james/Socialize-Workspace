package net.thenova.titan.spigot.module.doublejump.listeners;

import net.thenova.titan.spigot.data.compatability.model.CompParticle;
import net.thenova.titan.spigot.data.compatability.model.CompSound;
import net.thenova.titan.spigot.module.doublejump.handler.DJHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MoveEvent implements Listener {

    private final List<UUID> players = new ArrayList<>();

    @EventHandler
    public final void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if(event.isCancelled()
                || player.isFlying()
                || player.getGameMode() == GameMode.SPECTATOR
                || player.getGameMode() == GameMode.CREATIVE
                || !DJHandler.INSTANCE.isEnabled(player)
                || !player.hasPermission("titan.doublejump.user")) {
            return;
        }

        player.setAllowFlight(!this.players.contains(player.getUniqueId()));
        if(this.players.contains(player.getUniqueId())
                && player.isOnGround()) {
            this.players.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onFlyEvent(final PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();
        if(event.isCancelled()
                || player.isFlying()
                || player.getGameMode() == GameMode.SPECTATOR
                || player.getGameMode() == GameMode.CREATIVE
                || !DJHandler.INSTANCE.isEnabled(player)
                || !player.hasPermission("titan.doublejump.user")) {
            return;
        }

        if (!this.players.contains(player.getUniqueId())) {
            this.players.add(player.getUniqueId());

            player.setVelocity(player.getLocation().getDirection().multiply(4.0D).setY(1.5D));
            player.playSound(player.getLocation(), CompSound.BAT_TAKEOFF.getSound(), 1.0f, 1.0f);

            player.spawnParticle(CompParticle.FLAME.get(), player.getLocation(), 5, 0.1f, 0.1f, 0.1f, 1, 4);
        }

        player.setAllowFlight(false);
        event.setCancelled(true);
    }
}
