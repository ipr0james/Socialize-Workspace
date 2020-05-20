package net.thenova.titan.spigot.module.togglepvp.handler;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.UserHandler;
import org.bukkit.entity.Player;

public final class PlaceholderPVPStatus extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "titan_togglepvp";
    }

    @Override
    public String getAuthor() {
        return "ipr0james";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null){
            return "";
        }

        switch(identifier.toLowerCase()) {
            case "status":
                return (boolean )UserHandler.INSTANCE.getUser(player).getModule(UserPVP.class).get(new KeyPVPDisabled()) ? "disabled" : "enabled";
        }
        return null;
    }
}
