package net.thenova.titan.spigot.module.doublejump.handler;

import de.arraying.kotys.JSONArray;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum DJHandler {
    INSTANCE;

    private JSONFile file;

    private final List<String> worlds = new ArrayList<>();

    public final void load() {
        this.file = FileHandler.INSTANCE.loadJSONFile(DJDataFile.class);
        this.worlds.clear();

        final JSONArray array = this.file.getJSON().array("enabled-worlds");
        for(int i = 0; i < array.length(); i++) {
            this.worlds.add(array.string(i).toLowerCase());
        }
    }

    public final boolean isEnabled(final Player player) {
        return this.worlds.contains(player.getWorld().getName().toLowerCase());
    }
}
