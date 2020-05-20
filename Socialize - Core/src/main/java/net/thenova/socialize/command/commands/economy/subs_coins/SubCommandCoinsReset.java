package net.thenova.socialize.command.commands.economy.subs_coins;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCurrent;

import java.util.List;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@CommandUsage(
        min = 1,
        usage = "coins reset <@user>",
        description = "Reset a users coins to default amount."
)
public final class SubCommandCoinsReset extends Command {

    public SubCommandCoinsReset() {
        super("reset");

        this.addPermission(
                CommandPermission.discord(Permission.ADMINISTRATOR)
        );
    }

    @Override
    protected void execute(final Entity sender, final CommandContext context) {
        final List<Member> members = context.getMessage().getMentionedMembers();

        if(members.isEmpty()) {
            context.error("You must mention a user.");
            return;
        }

        final Member member = members.get(0);
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);
        if(entity == null || member.getUser().isBot() || member.getUser().isFake()) {
            context.error("Invalid user.");
            return;
        }

        entity.getBalance().set(new CoinsCurrent().value(), EntityBalance.Reason.ADMIN);
        context.reply(Embed.socialize("<@" + entity.getUserID() + "> coins have been reset. (" + new CoinsCurrent().value() + ")")).queue();
    }
}
