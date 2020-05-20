package net.thenova.socialize.command.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.economy.subs_coins.SubCommandCoinsGive;
import net.thenova.socialize.command.commands.economy.subs_coins.SubCommandCoinsReset;
import net.thenova.socialize.command.commands.economy.subs_coins.SubCommandCoinsSet;
import net.thenova.socialize.command.commands.economy.subs_coins.SubCommandCoinsTake;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
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
        min = 0,
        usage = "coins [user]",
        description = "Display yours or another users info/stats."
)
public final class CommandCoins extends Command {

    public CommandCoins() {
        super("coins","balance", "bal");

        this.addSubCommand(
                new SubCommandCoinsGive(),
                new SubCommandCoinsReset(),
                new SubCommandCoinsSet(),
                new SubCommandCoinsTake()
        );
    }

    @Override
    protected final void execute(Entity entity, CommandContext context) {
        final List<Member> mentioned = context.getMessage().getMentionedMembers();

        if(mentioned.isEmpty()) {
            this.sendEmbed(context, context.getMember());
        } else {
            this.sendEmbed(context, mentioned.get(0));
        }
    }

    private void sendEmbed(CommandContext context, Member member) {
        final EmbedBuilder builder = Embed.def();
        final Entity entity = EntityHandler.INSTANCE.getEntity(member);

        if(entity == null) {
            context.error("Invalid user provided.");
            return;
        }

        builder.setThumbnail("https://cdn.discordapp.com/emojis/601338090912481280.png?v=1");
        builder.setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
        builder.setFooter("Earn coins by chatting or voice calling", null);

        builder.appendDescription("**Coins** - " + UNumber.format(entity.getStats().fetch(new CoinsCurrent())) + Bot.INSTANCE.getEmoji("coins"));

        context.reply(builder.build()).queue();
    }
}