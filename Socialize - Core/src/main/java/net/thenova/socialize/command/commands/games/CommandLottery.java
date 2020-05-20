package net.thenova.socialize.command.commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.games.subs_lottery.SubCommandLotteryBuy;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.games.GameManager;
import net.thenova.socialize.games.lottery.TaskLottery;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.command.CommandPrefixData;
import net.thenova.socialize.guild.data.entries.games.GameLotteryData;
import net.thenova.socialize.util.task.TaskHandler;
import net.thenova.titan.library.util.UNumber;

import java.util.Map;
import java.util.Objects;
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
        min = 0,
        usage = "lottery [buy]",
        description = "Purchase lottery tickets for the daily lottery."
)
public final class CommandLottery extends Command {

    public CommandLottery() {
        super("lottery");

        this.addSubCommand(new SubCommandLotteryBuy());
    }

    /**
     * Execution method for the command once 'run' has completed execution checks.
     *
     * @param entity
     * @param context - Commands context
     */
    @Override
    protected final void execute(final Entity entity, final CommandContext context) {
        final EmbedBuilder builder = Embed.casino(context.getMember());
        final long seconds = Objects.requireNonNull(TaskHandler.INSTANCE.getTask(entity.getGuildID(), TaskLottery.class)).getDelay(TimeUnit.SECONDS);
        final Map<Long, Long> tickets = GameManager.INSTANCE.getLotteryData().getTickets(entity.getGuildID());

        long total = 0;
        for(Long count : tickets.values()) {
            total += count;
        }

        builder.setTitle("Lottery Information");
        builder.appendDescription("The lottery will be drawn in " + UNumber.getTimeShort(seconds) + ".\n" + Embed.Z);

        builder.addField("**Current Tickets**", "`" + total + "`", true);
        builder.addField("**Prize Pool**", "`"
                + (total * GuildHandler.INSTANCE.fetch(entity.getGuildID(), new GameLotteryData()).get(GameLotteryData.Type.TICKET_PRICE).asLong())
                + "`" + Bot.INSTANCE.getEmoji("coins") + "\n" + Embed.Z, true);

        builder.setFooter("Use `" + GuildHandler.INSTANCE.fetch(entity.getGuildID(), new CommandPrefixData()).get().asString() + "lottery buy [amount]` to purchase tickets to the daily lottery.", null);

        context.reply(builder.build()).queue();
    }
}
