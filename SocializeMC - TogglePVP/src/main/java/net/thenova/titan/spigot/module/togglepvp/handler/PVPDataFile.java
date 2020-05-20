package net.thenova.titan.spigot.module.togglepvp.handler;

import net.thenova.titan.library.file.json.JSONFileData;
import net.thenova.titan.spigot.module.ModuleHandler;

public class PVPDataFile implements JSONFileData {
    @Override
    public String name() {
        return "pvptoggle";
    }

    @Override
    public String path() {
        return ModuleHandler.INSTANCE.getDataFolder().toPath().toString();
    }

    @Override
    public ClassLoader loader() {
        return getClass().getClassLoader();
    }
}
