package net.thenova.titan.spigot.module.togglepvp.listeners;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.player.LandPlayer;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.util.cooldown.Cooldown;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.togglepvp.handler.PVPHandler;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;
import net.thenova.titan.spigot.util.UValidate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DamageEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public final void onDamage(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)
            || PVPHandler.INSTANCE.isBypass((Player) event.getEntity())) {
            return;
        }

        final LandsIntegration lands = PVPHandler.INSTANCE.getLands();
        final User damaged = UserHandler.INSTANCE.getUser((Player) event.getEntity());

        if(event.getDamager() instanceof Player) {
            final User damager = UserHandler.INSTANCE.getUser((Player) event.getDamager());
            LandPlayer lp;

            if((boolean) damaged.getModule(UserPVP.class).get(new KeyPVPDisabled())
                && ((lp = lands.getLandPlayer(damaged.getUUID())) == null || !lp.isInWar())) {
                if (!Cooldown.inCooldown(damager.getUUID().toString(), "damaged")) {
                    MessageHandler.INSTANCE.build("module.togglepvp.damage.damaged-disabled").send(damager);
                    new Cooldown(damager.getUUID().toString(), "damaged", 3);
                }

                event.setCancelled(true);
                return;
            }

            if((boolean) damager.getModule(UserPVP.class).get(new KeyPVPDisabled())
                    && ((lp = lands.getLandPlayer(damager.getUUID())) == null || !lp.isInWar())) {
                if(!Cooldown.inCooldown(damager.getUUID().toString(), "damager")) {
                    MessageHandler.INSTANCE.build("module.togglepvp.damage.damager-disabled").send(damager);
                    new Cooldown(damager.getUUID().toString(), "damager", 3);
                }

                event.setCancelled(true);
            }
            return;
        }

        if(event.getDamager() instanceof Projectile ) {
            final Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player) {
                final User damager = UserHandler.INSTANCE.getUser((Player) projectile.getShooter());
                LandPlayer lp;

                if((boolean) damaged.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damaged.getUUID())) == null || !lp.isInWar())) {
                    if(!Cooldown.inCooldown(damager.getUUID().toString(), "damaged")) {
                        MessageHandler.INSTANCE.build("module.togglepvp.damage.damaged-disabled").send(damager);
                        new Cooldown(damager.getUUID().toString(), "damaged", 3);
                    }

                    projectile.remove();
                    event.setCancelled(true);
                    return;
                }

                if((boolean) damager.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damager.getUUID())) == null || !lp.isInWar())) {
                    if(!Cooldown.inCooldown(damager.getUUID().toString(), "damager")) {
                        MessageHandler.INSTANCE.build("module.togglepvp.damage.damager-disabled").send(damager);
                        new Cooldown(damager.getUUID().toString(), "damager", 3);
                    }

                    projectile.remove();
                    event.setCancelled(true);
                }
            }
            return;
        }

        if(event.getDamager() instanceof ThrownPotion) {
            final ThrownPotion potion = (ThrownPotion) event.getDamager();
            if(potion.getShooter() instanceof Player) {
                final User damager = UserHandler.INSTANCE.getUser((Player) potion.getShooter());
                LandPlayer lp;

                if((boolean) damaged.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damaged.getUUID())) == null || !lp.isInWar())) {
                    if(!Cooldown.inCooldown(damager.getUUID().toString(), "damaged")) {
                        MessageHandler.INSTANCE.build("module.togglepvp.damage.damaged-disabled").send(damager);
                        new Cooldown(damager.getUUID().toString(), "damaged", 3);
                    }

                    event.setCancelled(true);
                    return;
                }

                if((boolean) damager.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damager.getUUID())) == null || !lp.isInWar())) {
                    if(!Cooldown.inCooldown(damager.getUUID().toString(), "damager")) {
                        MessageHandler.INSTANCE.build("module.togglepvp.damage.damager-disabled").send(damager);
                        new Cooldown(damager.getUUID().toString(), "damager", 3);
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public final void onDamage(final EntityDamageEvent event) {
        final EntityDamageEvent.DamageCause cause = event.getCause();
        if(event.isCancelled()
                || (!(event.getEntity() instanceof Player))
                || !(cause == EntityDamageEvent.DamageCause.LAVA
                    || cause == EntityDamageEvent.DamageCause.FIRE
                    || cause == EntityDamageEvent.DamageCause.FIRE_TICK)
                || PVPHandler.INSTANCE.isBypass((Player) event.getEntity())) {
            return;
        }

        final Location location = event.getEntity().getLocation();
        final List<Location> toCheck = Arrays.asList(location,
                location.clone().add(1, 0, 0),
                location.clone().add(1, 0, 1), //CORNER
                location.clone().add(1, 0, -1), // CORNER
                location.clone().add(-1, 0, 0),
                location.clone().add(-1, 0, 1),
                location.clone().add(-1, 0, -1),
                location.clone().add(0, 0, 1),
                location.clone().add(0, 0, -1));

        final Block block = toCheck.stream().filter(loc -> {
            final Block b = loc.getBlock();
            return UValidate.notNull(b)
                    && (b.getType() == Material.LAVA || b.getType() == Material.FIRE)
                    && b.hasMetadata("PLAYER");
        }).map(Location::getBlock).findFirst().orElse(null);

        if(block == null) {
            return;
        }

        try {
            final UUID uuid = UUID.fromString(block.getMetadata("PLAYER").get(0).asString());
            final User damager = UserHandler.INSTANCE.getUser(uuid);

            if (damager == null) {
                Titan.INSTANCE.getLogger().info("[Module] [TogglePVP] [DamageEvent] - Block had PLAYER but user returned null");
            } else {
                final LandsIntegration lands = PVPHandler.INSTANCE.getLands();
                LandPlayer lp;
                final User damaged = UserHandler.INSTANCE.getUser((Player) event.getEntity());

                if((boolean) damaged.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damaged.getUUID())) == null || !lp.isInWar())) {
                    event.getEntity().setFireTicks(0);
                    event.setCancelled(true);
                    return;
                }

                if((boolean) damager.getModule(UserPVP.class).get(new KeyPVPDisabled())
                        && ((lp = lands.getLandPlayer(damager.getUUID())) == null || !lp.isInWar())) {
                    event.getEntity().setFireTicks(0);
                    event.setCancelled(true);
                }
            }
        } catch (final IndexOutOfBoundsException ex) {
            Titan.INSTANCE.getLogger().info("[Module] [TogglePVP] [DamageEvent] - Block had PLAYER meta data but no data", ex);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onFish(final PlayerFishEvent event) {
        if(!(event.getCaught() instanceof Player)
                || PVPHandler.INSTANCE.isBypass((Player) event.getPlayer())) {
            return;
        }

        final LandsIntegration lands = PVPHandler.INSTANCE.getLands();
        final User damaged = UserHandler.INSTANCE.getUser((Player) event.getCaught());
        final User damager = UserHandler.INSTANCE.getUser(event.getPlayer());
        LandPlayer lp;

        if((boolean) damaged.getModule(UserPVP.class).get(new KeyPVPDisabled())
                && ((lp = lands.getLandPlayer(damaged.getUUID())) == null || !lp.isInWar())) {
            if(!Cooldown.inCooldown(damager.getUUID().toString(), "damaged")) {
                MessageHandler.INSTANCE.build("module.togglepvp.damage.damaged-disabled").send(damager);
                new Cooldown(damager.getUUID().toString(), "damaged", 3);
            }

            event.getHook().remove();
            event.setCancelled(true);
            return;
        }

        if((boolean) damager.getModule(UserPVP.class).get(new KeyPVPDisabled())
                && ((lp = lands.getLandPlayer(damager.getUUID())) == null || !lp.isInWar())) {
            if(!Cooldown.inCooldown(damager.getUUID().toString(), "damager")) {
                MessageHandler.INSTANCE.build("module.togglepvp.damage.damager-disabled").send(damager);
                new Cooldown(damager.getUUID().toString(), "damager", 3);
            }

            event.getHook().remove();
            event.setCancelled(true);
        }
    }

}
