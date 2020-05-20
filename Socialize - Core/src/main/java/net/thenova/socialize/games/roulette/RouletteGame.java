package net.thenova.socialize.games.roulette;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.games.roulette.bet.RouletteBet;
import net.thenova.socialize.games.roulette.position.RoulettePosition;
import net.thenova.socialize.games.roulette.position.RoulettePositionColor;
import net.thenova.socialize.games.roulette.position.RoulettePositionColumn;
import net.thenova.socialize.games.roulette.position.RoulettePositionHalf;
import net.thenova.titan.library.util.URandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public final class RouletteGame {

    private static final Map<Integer, RoulettePosition> positions = new HashMap<>();

    static {
        positions.put(0, new RoulettePosition(0, RoulettePositionColor.GREEN, RoulettePositionColumn.NULL, RoulettePositionHalf.NULL));
        positions.put(1, new RoulettePosition(1, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(2, new RoulettePosition(2, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(3, new RoulettePosition(3, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(4, new RoulettePosition(4, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(5, new RoulettePosition(5, RoulettePositionColor.RED, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(6, new RoulettePosition(6, RoulettePositionColor.BLACK, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(7, new RoulettePosition(7, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(8, new RoulettePosition(8, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(9, new RoulettePosition(9, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(10, new RoulettePosition(10, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(11, new RoulettePosition(11, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(12, new RoulettePosition(12, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(13, new RoulettePosition(13, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(14, new RoulettePosition(14, RoulettePositionColor.RED, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(15, new RoulettePosition(15, RoulettePositionColor.BLACK, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(16, new RoulettePosition(16, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.FIRST));
        positions.put(17, new RoulettePosition(17, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.FIRST));
        positions.put(18, new RoulettePosition(18, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.FIRST));
        positions.put(19, new RoulettePosition(19, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(20, new RoulettePosition(20, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(21, new RoulettePosition(21, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
        positions.put(22, new RoulettePosition(22, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(23, new RoulettePosition(23, RoulettePositionColor.RED, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(24, new RoulettePosition(24, RoulettePositionColor.BLACK, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
        positions.put(25, new RoulettePosition(25, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(26, new RoulettePosition(26, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(27, new RoulettePosition(27, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
        positions.put(28, new RoulettePosition(28, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(29, new RoulettePosition(29, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(30, new RoulettePosition(30, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
        positions.put(31, new RoulettePosition(31, RoulettePositionColor.BLACK, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(32, new RoulettePosition(32, RoulettePositionColor.RED, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(33, new RoulettePosition(33, RoulettePositionColor.BLACK, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
        positions.put(34, new RoulettePosition(34, RoulettePositionColor.RED, RoulettePositionColumn.FIRST, RoulettePositionHalf.SECOND));
        positions.put(35, new RoulettePosition(35, RoulettePositionColor.BLACK, RoulettePositionColumn.SECOND, RoulettePositionHalf.SECOND));
        positions.put(36, new RoulettePosition(36, RoulettePositionColor.RED, RoulettePositionColumn.THIRD, RoulettePositionHalf.SECOND));
    }

    private static final Map<Long, RouletteGame> GAMES = new HashMap<>(); // ChannelID, Game

    private final MessageChannel channel;
    private final ScheduledFuture task;

    private final List<RoulettePlayer> players = new ArrayList<>();

    private RouletteGame(MessageChannel channel) {
        this.channel = channel;
        this.task = Executors.newSingleThreadScheduledExecutor().schedule(this::play, 31, TimeUnit.SECONDS);

        RouletteGame.GAMES.put(channel.getIdLong(), this);
    }

    private void play() {
        final int number = URandom.r(0, 36);
        final List<RoulettePlayer> winners = this.players.stream().filter(player -> player.play(number)).collect(Collectors.toList());

        final EmbedBuilder builder = Embed.def();
        builder.setTitle("Roulette");
        builder.setThumbnail("https://media.discordapp.net/attachments/483346412495437834/612260021056700436/roulette.png");

        builder.appendDescription("The ball landed on: " + number + " " + positions.get(number).getColor().toString().toLowerCase() + "\n" + Embed.Z + "\n");

        if(winners.isEmpty()) {
            builder.appendDescription("**No winners!**");
            builder.setColor(Embed.EmbedColor.RED.get());
        } else {
            StringBuilder str = new StringBuilder();

            winners.forEach(player -> str.append(player.getMember().getEffectiveName())
                    .append(" **-** ")
                    .append(player.getWinnings())
                    .append(Bot.INSTANCE.getEmoji("coin")));

            builder.addField("Winners", str.toString(), false);
            builder.setColor(Embed.EmbedColor.GREEN.get());
        }

        this.channel.sendMessage(builder.build()).queue();
        RouletteGame.GAMES.remove(this.channel.getIdLong());
    }

    public static RouletteGame get(MessageChannel channel) {
        if(RouletteGame.GAMES.containsKey(channel.getIdLong())) {
            return RouletteGame.GAMES.get(channel.getIdLong());
        } else {
            return new RouletteGame(channel);
        }
    }

    public final void join(final Entity entity, final CommandContext context, final RouletteBet bet) {
        /*if(this.players.stream().anyMatch(player ->
                player.getMember().getUser().getIdLong() == context.getMember().getUser().getIdLong())) {
            context.error("You are already a part of the current roulette.");
            return;
        }*/

        entity.getBalance().take(bet.getBet(), EntityBalance.Reason.SYSTEM);
        this.players.add(new RoulettePlayer(entity, context.getMember(), bet));

        final EmbedBuilder builder = Embed.casino(context.getMember());
        builder.setTitle("Roulette");
        builder.appendDescription("You have placed a bet of " + bet.getBet() + Bot.INSTANCE.getEmoji("coin") + " on `" + bet.getValue() + "`");

        builder.setFooter("Time remaining: " + task.getDelay(TimeUnit.SECONDS) + " seconds", null);
        context.reply(builder.build()).queue();
    }
}
