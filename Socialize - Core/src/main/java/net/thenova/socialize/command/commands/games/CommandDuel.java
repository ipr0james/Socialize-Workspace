package net.thenova.socialize.command.commands.games;

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
import net.thenova.socialize.entities.modules.stat_keys.coins.CoinsCasinoLoss;
import net.thenova.socialize.util.response.ResponseReaction;
import net.thenova.titan.library.util.URandom;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        usage = "duel <user> <bet>",
        description = "Tag another member to duel them."
)
public final class CommandDuel extends Command {

    public CommandDuel() {
        super("duel");
    }

    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final List<Member> mentioned = context.getMessage().getMentionedMembers();

        if (mentioned.isEmpty()) {
            context.error("You must mention at least 1 member.");
            return;
        }

        final Member member = mentioned.get(0);
        final long memberID = member.getUser().getIdLong();

        if(memberID == entity.getUserID()) {
            context.error("You cannot challenge yourself.");
            return;
        }

        long bet;

        try {
            bet = Long.parseLong(context.getArgument(1));
        } catch (NumberFormatException ex) {
            context.error("Your bet must be a number.");
            return;
        }

        if(bet < 1) {
            context.error("The minimum bet is `1` " + Bot.INSTANCE.getEmoji("coins"));
            return;
        }

        if(entity.getBalance().fetch() < bet) {
            context.error("You do not have enough coins to bet this much.");
            return;
        }

        final EmbedBuilder builder = Embed.casino(context.getMember());

        builder.setTitle("Duel");
        builder.appendDescription("<@" + memberID + "> you have been challenged to duel by <@" + entity.getUserID() + ">" +
                "\n" + Embed.Z +
                "\n**Bet:** " + bet + Bot.INSTANCE.getEmoji("coins"));

        context.reply("<@" + memberID + ">");
        context.reply(builder.build()).queue(message -> {
            ResponseReaction.create(message, member, false)
                    .reaction(Bot.INSTANCE.getEmoji("gang_tick"), click -> {
                        final Entity other = EntityHandler.INSTANCE.getEntity(member);
                        assert other != null;

                        if(other.getBalance().fetch() < bet) {
                            message.editMessage(Embed.error(member).appendDescription("You do not have enough coins to take this duel.").build()).queue();
                            ResponseReaction.remove(message.getIdLong());
                            return;
                        }

                        entity.getBalance().take(bet, EntityBalance.Reason.SYSTEM);
                        other.getBalance().take(bet, EntityBalance.Reason.SYSTEM);

                        builder.setDescription(Embed.Z + "\n<@" + entity.getUserID() + "> is dueling <@" + memberID + ">\n" + Embed.Z);

                        message.editMessage(builder.build()).queue(msg -> {
                            Executors.newSingleThreadScheduledExecutor()
                                    .schedule(() -> {
                                        final boolean winner = URandom.nextBoolean();
                                        builder.setDescription(Embed.Z + "\n<@" + (winner ? entity.getUserID() : memberID) + "> has won "
                                                + (bet * 2) + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z);

                                        if(winner) {
                                            entity.getBalance().add(bet * 2, EntityBalance.Reason.CASINO);
                                            other.getStats().increment(new CoinsCasinoLoss(), bet);
                                        } else {
                                            other.getBalance().add(bet * 2, EntityBalance.Reason.CASINO);
                                            entity.getStats().increment(new CoinsCasinoLoss(), bet);
                                        }

                                        msg.editMessage(builder.build()).queue();
                                    }, 2, TimeUnit.SECONDS);

                        });
                        ResponseReaction.remove(message.getIdLong());
                    })
                    .reaction(Bot.INSTANCE.getEmoji("gang_cross"), click -> {
                        message.editMessage(Embed.casino(context.getMember())
                                .appendDescription("<@" + memberID + "> has denied your duel invitation")
                                .build())
                                .queue();
                        ResponseReaction.remove(message.getIdLong());
                    });
        });
    }
}
