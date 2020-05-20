package net.thenova.socialize.command.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCurrent;
import net.thenova.titan.library.util.UNumber;

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
        usage = "pay <@member> <amount>",
        description = "Pay another member coins."
)
public final class CommandPay extends Command {

    public CommandPay() {
        super("pay", "give");
    }

    @Override
    protected final void execute(Entity entity, CommandContext context) {
        final List<Member> members = context.getMessage().getMentionedMembers();

        if(members.isEmpty()) {
            context.error("You must mention a user.");
            return;
        }

        final Member member = members.get(0);
        if(member.getUser().getIdLong() == entity.getUserID()) {
            context.error("You cannot pay coins to yourself.");
            return;
        }

        if(!UNumber.isLong(context.getArgument(1))) {
            context.error("Invalid amount provided.");
            return;
        }

        final Long value = Long.valueOf(context.getArgument(1));
        if(value < 1) {
            context.error("Value must be greater than zero.");
            return;
        }

        if(entity.getStats().fetch(new CoinsCurrent()) < value) {
            context.error("Insufficient coins!");
            return;
        }

        final Entity other = EntityHandler.INSTANCE.getEntity(member);
        if(other == null) {
            context.error("Invalid user.");
            return;
        }

        entity.getBalance().take(value, EntityBalance.Reason.SYSTEM);
        other.getBalance().add(value, EntityBalance.Reason.SYSTEM);
        context.reply("<@" + member.getUser().getIdLong() + ">");

        final EmbedBuilder builder = Embed.def();

        builder.setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
        builder.appendDescription("You have received " + value + Bot.INSTANCE.getEmoji("coins") + " from <@" + entity.getUserID() + ">");

        context.reply(builder.build()).queue();
    }
}
