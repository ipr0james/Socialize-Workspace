package net.thenova.titan.spigot.module.togglepvp.handler;

import de.arraying.kotys.JSONArray;
import lombok.Getter;
import me.angeschossen.lands.api.integration.LandsIntegration;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.spigot.TitanSpigot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum PVPHandler {
    INSTANCE;

    @Getter private LandsIntegration lands;

    private JSONFile file;

    private final List<String> worlds = new ArrayList<>();

    public final void load() {
        this.file = FileHandler.INSTANCE.loadJSONFile(PVPDataFile.class);
        this.worlds.clear();

        final JSONArray array = this.file.getJSON().array("bypass-worlds");
        for(int i = 0; i < array.length(); i++) {
            this.worlds.add(array.string(i).toLowerCase());
        }

        this.lands = new LandsIntegration(TitanSpigot.INSTANCE.getPlugin(), false);
    }

    public final boolean isBypass(final Player player) {
        return this.worlds.contains(player.getWorld().getName().toLowerCase());
    }
}
