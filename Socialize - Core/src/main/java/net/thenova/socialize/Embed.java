package net.thenova.socialize;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

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
public class Embed {

    public final static String Z = EmbedBuilder.ZERO_WIDTH_SPACE;

    public static EmbedBuilder def() {
        return new EmbedBuilder()
                .setColor(0x3598DB)
                .setDescription(Embed.Z + "\n");
    }

    public static EmbedBuilder socialize() {
        return new EmbedBuilder()
                .setColor(0x3598DB)
                .setTitle("Socialize Core")
                .setThumbnail("https://cdn.discordapp.com/attachments/585549876637728791/604234874122928148/512-8mb.gif")
                .setDescription(EmbedBuilder.ZERO_WIDTH_SPACE + "\n");
    }

    public static EmbedBuilder socialize(String message) {
        return Embed.socialize().appendDescription(message);
    }

    public static EmbedBuilder error(Member member) {
        final EmbedBuilder builder =  new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("**Error!**")
                .setThumbnail("https://cdn.discordapp.com/emojis/601404540444737536.png")
                .setDescription(EmbedBuilder.ZERO_WIDTH_SPACE + "\n");

        if(member != null) {
            builder.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl());
        }

        return builder;
    }

    public static MessageEmbed error(Member member, String message) {
        return Embed.error(member).appendDescription(message).build();
    }

    public static EmbedBuilder gang(Member member) {
        return new EmbedBuilder()
                .setColor(0x6581ff)
                .setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl())
                .setDescription(Embed.Z + "\n")
                .setThumbnail("https://cdn.discordapp.com/emojis/601338031131066370.png");
    }

    public static MessageEmbed gang(Member member, String message) {
        return Embed.gang(member).appendDescription(message).build();
    }

    public static EmbedBuilder casino(Member member) {
        return new EmbedBuilder()
                .setColor(0xae6cf5)
                //.setColor(Color.MAGENTA)
                .setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl())
                .setDescription(Embed.Z + "\n")
                .setThumbnail("https://cdn.discordapp.com/emojis/601397099220828160.png");
    }

    public enum EmbedColor {
        GREEN(0x5ffc47),
        RED(0xfc4747);

        private final int color;

        EmbedColor(final int color) {
            this.color = color;
        }

        public final int get() {
            return this.color;
        }
    }
}
