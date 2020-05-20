package net.thenova.socialize.games.lottery;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.games.GameManager;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;
import net.thenova.socialize.guild.data.entries.command.CommandPrefixData;
import net.thenova.socialize.guild.data.entries.games.GameLotteryData;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.util.URandom;

import java.util.ArrayList;
import java.util.Collections;
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
public class TaskLottery implements Runnable {

    private final long guildID;

    public TaskLottery(final long guildID) {
        this.guildID = guildID;
    }

    @Override
    public void run() {
        final List<Long> users = new ArrayList<>();

        GameManager.INSTANCE.getLotteryData().getTickets(this.guildID).forEach((user, count) -> {
            for(int i = 0; i < count; i++) {
                users.add(user);
            }
        });

        if(users.isEmpty()) {
            return;
        }

        Collections.shuffle(users);

        final Entity entity = this.getEntity(users);
        if(entity == null) {
            Titan.INSTANCE.getLogger().info("[TaskLottery] Error inside guild {}, Lottery failed to find a valid entity in 100 attempts", this.guildID);
            return;
        }

        final Member member = entity.getMember();
        assert member != null;

        final long prize = users.size() * GuildHandler.INSTANCE.fetch(this.guildID, new GameLotteryData()).get(GameLotteryData.Type.TICKET_PRICE).asLong();

        entity.getBalance().add(prize, EntityBalance.Reason.SYSTEM);
        GameManager.INSTANCE.getLotteryData().resetGuild(this.guildID);

        final TextChannel channel = GuildHandler.INSTANCE.fetch(this.guildID, new GuildChannelsData()).getChannel(GuildChannelsData.Type.LOUNGE);
        if(channel == null) {
            return;
        }
        final EmbedBuilder builder = Embed.casino(member);
        builder.appendDescription("<@" + entity.getUserID() + "> has won `" + prize + "`" + Bot.INSTANCE.getEmoji("coins") + " from the daily lottery!\n" + Embed.Z);
        builder.setFooter("Use `" + GuildHandler.INSTANCE.fetch(this.guildID, new CommandPrefixData()).get().asString()
                + "lottery buy [amount]` to purchase tickets!" , null);

        channel.sendMessage("<@" + entity.getUserID() + ">").queue();
        channel.sendMessage(builder.build()).queue();
    }

    private Entity getEntity(List<Long> users) {
        final Guild guild = Bot.getJDA().getGuildById(this.guildID);
        assert guild != null;
        Entity entity = null;
        int i = 0;

        while(entity == null || i < 100) {
            long id = users.get(URandom.r(0, users.size() - 1));
            i++;

            final Entity temp = EntityHandler.INSTANCE.getEntity(guild, id);
            if(temp != null && guild.getMemberById(id) != null) {
                entity = temp;
            }
        }

        return entity;
    }
}
