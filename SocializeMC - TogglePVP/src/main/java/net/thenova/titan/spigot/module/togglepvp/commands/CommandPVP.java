package net.thenova.titan.spigot.module.togglepvp.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.togglepvp.commands.subs_pvp.SubCommandPVPDisable;
import net.thenova.titan.spigot.module.togglepvp.commands.subs_pvp.SubCommandPVPEnable;
import net.thenova.titan.spigot.module.togglepvp.commands.subs_pvp.SubCommandPVPToggle;
import net.thenova.titan.spigot.users.user.User;

public final class CommandPVP extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandPVP() {
        super("pvp");

        this.addSubCommand(
                new SubCommandPVPDisable(),
                new SubCommandPVPEnable(),
                new SubCommandPVPToggle()
        );
    }

    @Override
    public final void execute(final User user, final CommandContext commandContext) {
        MessageHandler.INSTANCE.build("module.togglepvp.help").send(user);
    }

    @Override
    public boolean hasPermission(final User user) {
        return user.hasPermission("titan.command.pvp");
    }
}
