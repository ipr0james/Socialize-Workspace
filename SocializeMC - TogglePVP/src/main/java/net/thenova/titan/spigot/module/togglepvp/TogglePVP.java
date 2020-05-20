package net.thenova.titan.spigot.module.togglepvp;

import de.arraying.kotys.JSONArray;
import net.thenova.titan.library.command.data.Command;
import net.thenova.titan.library.database.connection.IDatabase;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.togglepvp.commands.CommandPVP;
import net.thenova.titan.spigot.module.togglepvp.handler.PVPHandler;
import net.thenova.titan.spigot.module.togglepvp.handler.PlaceholderPVPStatus;
import net.thenova.titan.spigot.module.togglepvp.handler.TaskActionBar;
import net.thenova.titan.spigot.module.togglepvp.listeners.BlockEvent;
import net.thenova.titan.spigot.module.togglepvp.listeners.ConnectionEvent;
import net.thenova.titan.spigot.module.togglepvp.listeners.DamageEvent;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.plugin.IPlugin;
import net.thenova.titan.spigot.users.user.module.UserModule;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TogglePVP implements IPlugin {

    private TaskActionBar task;

    @Override
    public String name() {
        return "togglepvp";
    }

    @Override
    public void load() {
        PVPHandler.INSTANCE.load();

        new PlaceholderPVPStatus().register();
    }

    @Override
    public final void messages(final MessageHandler handler) {
        handler.add("module.togglepvp.help", new JSONArray()
                .append(
                    "&7",
                    "&d/pvp enable &7- Set PVP to enabled",
                    "&d/pvp disable &7- Set PVP to disabled",
                    "&d/pvp toggle &7- Toggle your PVP status",
                    "&7"
        ));

        handler.add("module.togglepvp.disabled-world", "%prefix.error% You cannot toggle your PVP status in this world.");
        handler.add("module.togglepvp.in-war", "%prefix.error% You cannot toggle your PVP status when in a Lands war.");
        handler.add("module.togglepvp.in-pvp", "%prefix.error% You cannot toggle your PVP status when you're in combat.");

        handler.add("module.togglepvp.disabled","%prefix.info% PVP has been disabled.");
        handler.add("module.togglepvp.enabled","%prefix.info% PVP has been enabled.");
        handler.add("module.togglepvp.toggle","%prefix.info% PVP has been set to %status%.");

        handler.add("module.togglepvp.damage.damaged-disabled", "%prefix.error% That player has PVP disabled.");
        handler.add("module.togglepvp.damage.damager-disabled", "%prefix.error% You have PVP disabled. Use &c/pvp enable&7.");

        handler.add("module.togglepvp.actionbar", "You currently have PVP %status%");
    }

    @Override
    public final void reload() {
        //TaskHandler.INSTANCE.addTask(this.getClass(), Bukkit.getScheduler().runTaskTimer(TitanSpigot.INSTANCE.getPlugin(), (this.task = new TaskActionBar()), 20, 20).getTaskId());
    }

    @Override
    public final void shutdown() {

    }

    @Override
    public final IDatabase database() {
        return null;
    }

    @Override
    public final List<DatabaseTable> tables() {
        return null;
    }

    @Override
    public final List<Listener> listeners() {
        return Arrays.asList(new BlockEvent(), new ConnectionEvent(), new DamageEvent());
    }

    @Override
    public final List<Command> commands() {
        return Collections.singletonList(new CommandPVP());
    }

    @Override
    public final List<Class<? extends UserModule>> user() {
        return Collections.singletonList(UserPVP.class);
    }
}
