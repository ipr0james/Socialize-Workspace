package net.thenova.socialize.command.commands.system.roles;

import net.thenova.socialize.command.CommandTemplateHelp;
import net.thenova.socialize.command.commands.system.roles.subs_roles.CommandSystemRoleInfo;
import net.thenova.socialize.command.commands.system.roles.subs_roles.CommandSystemRoleList;
import net.thenova.socialize.command.commands.system.roles.subs_roles.CommandSystemRoleSet;

public final class CommandSystemRole extends CommandTemplateHelp {

    public CommandSystemRole() {
        super("role", "roles");

        this.addSubCommand(
                new CommandSystemRoleInfo(),
                new CommandSystemRoleList(),
                new CommandSystemRoleSet()
        );
    }
}
