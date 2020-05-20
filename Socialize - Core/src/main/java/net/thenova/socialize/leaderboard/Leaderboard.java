package net.thenova.socialize.leaderboard;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.guild.data.entries.GuildChannelsData;

import java.awt.*;
import java.time.Instant;
import java.util.Map;

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
public abstract class Leaderboard {

    private static final String GAP = Bot.INSTANCE.getEmoji("blank") + "\n" + EmbedBuilder.ZERO_WIDTH_SPACE + "\n";

    protected final long guildID;

    private final GuildChannelsData.Type type;
    private final EmbedBuilder builder;
    private final String emoji;

    public Leaderboard(final long guildID, final GuildChannelsData.Type type, final String title, final String emoji) {
        this.guildID = guildID;
        this.type = type;

        this.builder = Embed.socialize().setColor(Color.GRAY);

        this.builder.setTitle(Bot.INSTANCE.getEmoji("lbtrophy") + " ** Leaderboard [__" + title + "__]**");
        this.builder.setFooter("All-time Gang leaderboard | Leaderboard updates every 5 minutes", Bot.getJDA().getSelfUser().getEffectiveAvatarUrl());

        this.emoji = Bot.INSTANCE.getEmoji(emoji);
    }

    public abstract Map<String, Long> fetch(); // Name, Result

    void refresh() {
        this.builder.clearFields();
        Map<String, Long> results = this.fetch();

        int i = 1;
        final StringBuilder ranking = new StringBuilder();
        final Guild guild = Bot.getJDA().getGuildById(this.guildID);
        if(guild == null) {
            return;
        }

        for(Map.Entry<String, Long> value : results.entrySet()) {
            if(i > 10) {
                break;
            }

            try {
                if(guild.getMemberById(Long.parseLong(value.getKey())) == null) {
                    continue;
                }
            } catch (NumberFormatException ignored) {}

            ranking.append("**")
                    .append(i)
                    .append(".** ")
                    .append(value.getKey())
                    .append(" **-** `")
                    .append(value.getValue())
                    .append("`")
                    .append(this.emoji)
                    .append(Leaderboard.GAP);
            i++;
        }

        while(i <= 10) {
            ranking.append("**")
                    .append(i)
                    .append(".** N/A **-** `0`")
                    .append(this.emoji)
                    .append(Leaderboard.GAP);

            i++;
        }

        this.builder.addField("**__Rankings__**", ranking.toString(), true);
        this.builder.setTimestamp(Instant.now());

        final TextChannel channel = GuildHandler.INSTANCE.fetch(this.guildID, new GuildChannelsData()).getChannel(this.type);
        if(channel == null) {
            GuildHandler.INSTANCE.messageError(this.guildID, "Leaderboard `" + this.type.toString() + "` could not be updated as the given channel was invalid.");
            return;
        }

        if(!channel.hasLatestMessage()
                || channel.retrieveMessageById(channel.getLatestMessageId()).complete().getAuthor().getIdLong() != Bot.getJDA().getSelfUser().getIdLong()) {
            channel.sendMessage(Embed.socialize().build()).queue();
        }

        channel.editMessageById(channel.getLatestMessageIdLong(), this.builder.build()).queue();
    }
}
