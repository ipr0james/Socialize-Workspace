package net.thenova.titan.spigot.module.doublejump;

import net.thenova.titan.library.command.data.Command;
import net.thenova.titan.library.database.connection.IDatabase;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.doublejump.handler.DJHandler;
import net.thenova.titan.spigot.module.doublejump.listeners.MoveEvent;
import net.thenova.titan.spigot.plugin.IPlugin;
import net.thenova.titan.spigot.users.user.module.UserModule;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

public final class DoubleJump implements IPlugin {
    @Override
    public String name() {
        return "DoubleJump";
    }

    @Override
    public void load() {
        DJHandler.INSTANCE.load();
    }

    @Override
    public void messages(final MessageHandler handler) {

    }

    @Override
    public void reload() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public IDatabase database() {
        return null;
    }

    @Override
    public List<DatabaseTable> tables() {
        return null;
    }

    @Override
    public List<Listener> listeners() {
        return Collections.singletonList(new MoveEvent());
    }

    @Override
    public List<Command> commands() {
        return null;
    }

    @Override
    public List<Class<? extends UserModule>> user() {
        return null;
    }
}
