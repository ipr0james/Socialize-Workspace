package net.thenova.titan.spigot.module.doublejump.handler;

import net.thenova.titan.library.file.json.JSONFileData;
import net.thenova.titan.spigot.module.ModuleHandler;

public final class DJDataFile implements JSONFileData {
    @Override
    public String name() {
        return "doublejump";
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
