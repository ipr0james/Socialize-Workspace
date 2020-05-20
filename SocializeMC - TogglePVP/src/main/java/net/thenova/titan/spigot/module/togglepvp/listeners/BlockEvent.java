package net.thenova.titan.spigot.module.togglepvp.listeners;

import net.thenova.titan.spigot.TitanSpigot;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class BlockEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlace(final PlayerBucketEmptyEvent event) {
        if(event.isCancelled()
            || event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        event.getBlock().setMetadata("PLAYER", new FixedMetadataValue(TitanSpigot.INSTANCE.getPlugin(), event.getPlayer().getUniqueId().toString()));
    }

    @EventHandler
    public final void onIgnite(final BlockIgniteEvent event) {
        if(event.isCancelled()
                || event.getPlayer() == null) {
            return;
        }

        event.getBlock().setMetadata("PLAYER", new FixedMetadataValue(TitanSpigot.INSTANCE.getPlugin(), event.getPlayer().getUniqueId().toString()));
    }

    @EventHandler
    public final void onSpread(final BlockFromToEvent event) {
        if(event.isCancelled()
            || event.getBlock().getType() != Material.LAVA
            || !event.getBlock().hasMetadata("PLAYER")) {
            return;
        }

        event.getToBlock().setMetadata("PLAYER", new FixedMetadataValue(TitanSpigot.INSTANCE.getPlugin(), event.getBlock().getMetadata("PLAYER").get(0).asString()));
    }
}
