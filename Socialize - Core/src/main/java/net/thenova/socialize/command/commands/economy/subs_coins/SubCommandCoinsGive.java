package net.thenova.socialize.command.commands.economy.subs_coins;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;

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
        min = 2,
        usage = "coins give <@user> <amount>",
        description = "Give coins to a user"
)
public final class SubCommandCoinsGive extends Command {

    public SubCommandCoinsGive() {
        super("give");

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

        if(context.getArgument(1).contains("-")) {
            context.error("You cannot give a negative value.");
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(context.getArgument(1));
        } catch (NumberFormatException ex) {
            context.error("Amount must be a number.");
            return;
        }

        final Member member = members.get(0);
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);
        if(entity == null || member.getUser().isBot() || member.getUser().isFake()) {
            context.error("Invalid user.");
            return;
        }

        entity.getBalance().add(amount, EntityBalance.Reason.ADMIN);
        context.reply(Embed.socialize("<@" + entity.getUserID() + "> balance is now "
                        + entity.getBalance().fetch() + Bot.INSTANCE.getEmoji("coins") + " (+" + amount + ")"))
                .queue();
    }
}
