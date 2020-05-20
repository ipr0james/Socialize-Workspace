package net.thenova.titan.spigot.module.togglepvp.commands.subs_pvp;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import me.angeschossen.lands.api.player.LandPlayer;
import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.togglepvp.handler.PVPHandler;
import net.thenova.titan.spigot.module.togglepvp.user.UserPVP;
import net.thenova.titan.spigot.module.togglepvp.user.data_keys.KeyPVPDisabled;
import net.thenova.titan.spigot.users.user.User;

public final class SubCommandPVPToggle extends SpigotCommand<User> implements CommandPermission<User> {

    public SubCommandPVPToggle() {
        super("toggle");
    }

    @Override
    public final void execute(final User user, final CommandContext commandContext) {
        if(PVPHandler.INSTANCE.isBypass(user.getPlayer())) {
            MessageHandler.INSTANCE.build("module.togglepvp.disabled-world").send(user);
            return;
        }

        final LandPlayer lp;
        if((lp = PVPHandler.INSTANCE.getLands().getLandPlayer(user.getUUID())) != null
                && lp.isInWar()) {
            MessageHandler.INSTANCE.build("module.togglepvp.in-war").send(user);
            return;
        }

        if(CombatUtil.isInCombat(user.getPlayer())) {
            MessageHandler.INSTANCE.build("module.togglepvp.in-pvp").send(user);
            return;
        }

        final UserPVP pvp = user.getModule(UserPVP.class);
        final boolean value = (boolean) pvp.get(new KeyPVPDisabled());

        user.getModule(UserPVP.class).update(new KeyPVPDisabled(), !value);
        MessageHandler.INSTANCE.build("module.togglepvp.toggle")
                .placeholder(new Placeholder("status", value ? "enabled" : "disabled"))
                .send(user);
    }

    @Override
    public final boolean hasPermission(final User user) {
        return user.hasPermission("titan.command.pvp.toggle");
    }
}