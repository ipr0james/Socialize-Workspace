package net.thenova.socialize.command.commands.gang.officer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangData;
import net.thenova.socialize.gangs.gang.GangRole;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgrade;
import net.thenova.socialize.util.response.ResponseMessage;
import net.thenova.socialize.util.response.ResponseReaction;
import net.thenova.titan.library.util.UNumber;

import java.util.Arrays;
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
        usage = "gang shop",
        description = "Purchase upgrades or a name change for the Gang."
)
public final class SubCommandGangShop extends CommandTemplateGang {

    private final List<String> blocked;

    public SubCommandGangShop() {
        super("shop", GangRole.OFFICER);

        this.blocked = Arrays.asList("@", "#", ":", "```", "discordtag", "everyone", "here", "temp");
    }

    @Override
    public void run(Gang gang, Entity entity, CommandContext context) {
        final EmbedBuilder builder = Embed.def();
        builder.setThumbnail("https://cdn.discordapp.com/attachments/585549876637728791/603464825431130122/shopping-cart.png");
        builder.setTitle("**Gang Shop**");
        builder.setColor(0x6581ff);
        builder.setFooter("Enter a number in chat to purchase or type 'cancel' to exit.", null);

        builder.addField("**1.** Change Gang Name", "Purchase a name change for your gang!\n" + Embed.Z, false);

        final List<GangUpgrade> upgrades = gang.getUpgrades().getUpgrades();

        int i = 1;

        for(GangUpgrade upgrade : gang.getUpgrades().getUpgrades()) {
            i++;
            if(upgrade.isMaxLevel()) {
                builder.addField("**" + i + ".** " + upgrade.getName(),
                        "Fully Upgraded\n" + Embed.Z,
                        false);
                continue;
            }

            StringBuilder desc = new StringBuilder();
            Arrays.stream(upgrade.getDescription()).forEach(str -> desc.append(str).append("\n"));

            builder.addField("**" + i + ".** " + upgrade.getName(),
                    desc.toString() + Embed.Z,
                    false);
        }

        final Member member = context.getMember();

        context.reply(builder.build()).queue(message -> {
            final EmbedBuilder embed = Embed.def();
            embed.setThumbnail("https://cdn.discordapp.com/attachments/585549876637728791/603464933396709416/tag.png");
            embed.setTitle("Gang Shop");
            embed.setColor(0x6581ff);

            ResponseMessage.create(message, member, response -> {
                final String msg = response.getContentRaw();

                switch(msg.toLowerCase()) {
                    case "1":
                        ResponseMessage.remove(message.getIdLong());
                        this.purchaseName(message, gang, context);
                        return;
                    case "cancel":
                        embed.setDescription(Embed.Z + "\nAction has been cancelled..");
                        message.editMessage(embed.build()).queue();

                        ResponseMessage.remove(message.getIdLong());
                }

                int val = 1;
                for(GangUpgrade upgrade : upgrades) {
                    val++;
                    if(msg.equalsIgnoreCase(val + "")) {
                        ResponseMessage.remove(message.getIdLong());
                        this.purchase(message, gang, upgrade, context);
                        break;
                    }
                }
            }).cancellable(30, () -> {
                message.editMessage(Embed.error(member).appendDescription("Action has expired.").build()).queue();
            });;
        });
    }

    private void purchaseName(Message message, Gang gang, CommandContext context) {
        final Member member = context.getMember();
        final int cost = 25000;
        final EmbedBuilder embed = Embed.def();

        embed.setThumbnail("https://cdn.discordapp.com/attachments/585549876637728791/603464933396709416/tag.png");
        embed.setTitle("Purchase Name Change");

        embed.appendDescription("Purchase to change your gangs name.\n" + Embed.Z + "\n**Cost:** " + UNumber.format(cost));

        message.editMessage(embed.build())
                .queue(click -> {
                    final ResponseReaction response = ResponseReaction.create(message, member, false);

                    response.reaction(Bot.INSTANCE.getEmoji("gang_tick"), emoji -> {
                        embed.setDescription(Embed.Z + "\nEnter your new Gang name in to chat.");
                        message.editMessage(embed.build()).queue(edit -> {
                            ResponseReaction.remove(message.getIdLong());

                            ResponseReaction.create(edit, member, false)
                                    .reaction(Bot.INSTANCE.getEmoji("gang_cross"),
                                            action -> {
                                                ResponseMessage.remove(message.getIdLong());
                                                ResponseReaction.remove(message.getIdLong());

                                                embed.setDescription(Embed.Z + "\nAction has ben cancelled..");
                                                message.editMessage(embed.build()).queue();
                                            });

                            ResponseMessage.create(message, member, msg -> {
                                final String name = msg.getContentRaw();

                                if(cost > gang.getBank().fetch()) {
                                    ResponseMessage.remove(message.getIdLong());
                                    ResponseReaction.remove(message.getIdLong());
                                    message.editMessage(Embed.error(member)
                                            .appendDescription("Your gang does not have enough funds to purchase this.")
                                            .setFooter("Use `" + context.getPrefix() + "gang deposit <amount>` to add funds.", null)
                                            .build())
                                            .queue();
                                    return;
                                }

                                if(name.length() > 15) {
                                    context.error("Gang name must be less than 15 characters including spaces.");
                                    return;
                                }

                                for(String block : this.blocked) {
                                    if (name.contains(block)) {
                                        context.error("Gang name cannot contain `" + block + "`.");
                                        return;
                                    }
                                }

                                if(name.isEmpty()) {
                                    context.error("Please enter a gang name.");
                                    return;
                                }

                                if (GangManager.INSTANCE.isGang(context.getGuild().getIdLong(), name)) {
                                    context.error("A gang with that name already exists.");
                                    return;
                                }

                                gang.getData().set(GangData.Type.NAME, name);
                                gang.getBank().take(cost);
                                ResponseMessage.remove(message.getIdLong());
                                ResponseReaction.remove(message.getIdLong());

                                embed.setDescription("Your gang name has been changed to `" + name + "`");
                                edit.editMessage(embed.build()).queue();
                            }).cancellable(30, () -> {
                                message.editMessage(Embed.error(member).appendDescription("Action has expired.").build()).queue();
                            });
                        });
                    });

                    this.cancel(click, embed);
                });
    }

    private void purchase(Message message, Gang gang, GangUpgrade upgrade, CommandContext context) {
        final Member member = context.getMember();
        final EmbedBuilder embed = Embed.def();

        if(upgrade.isMaxLevel()) {
            message.editMessage(Embed.error(member).appendDescription("This upgrade is already max level.").build()).queue();
            return;
        }

        final StringBuilder desc = new StringBuilder();
        final int next = upgrade.getLevel() + 1;
        embed.setThumbnail("https://cdn.discordapp.com/attachments/585549876637728791/603464933396709416/tag.png");
        embed.setTitle("Purchase Upgrade - " + upgrade.getName());

        Arrays.stream(upgrade.getDescription())
                .forEach(str -> desc.append(str).append("\n"));
        desc.append(Embed.Z).append("\n");

        Arrays.stream(upgrade.description(next))
                .forEach(str -> desc.append(str).append("\n"));

        desc.append("\n")
                .append(Embed.Z)
                .append("\n**Cost:** ")
                .append(upgrade.isMaxLevel() ? "Max Level" : upgrade.cost(next))
                .append(Bot.INSTANCE.getEmoji("coins"));

        embed.appendDescription(desc.toString());

        message.editMessage(embed.build())
                .queue(click -> {
                    if(!upgrade.isMaxLevel()) {
                        final ResponseReaction response = ResponseReaction.create(message, member, false);

                        response.reaction(Bot.INSTANCE.getEmoji("gang_tick"), emoji -> {
                            if (upgrade.cost(next) > gang.getBank().fetch()) {
                                ResponseReaction.remove(message.getIdLong());
                                message.editMessage(Embed.error(member)
                                        .appendDescription("Your gang does not have enough funds to purchase this.\n" + Embed.Z)
                                        .setFooter("Use `" + context.getPrefix() + "gang deposit <amount>` to add funds.", null)
                                        .build())
                                        .queue();
                                return;
                            }

                            upgrade.upgrade(gang, upgrade.getLevel() + 1);

                            embed.setDescription(Embed.Z + "\nYou have upgraded `" + upgrade.getName() + "` to level `" + upgrade.getLevel() + "`");
                            message.editMessage(embed.build()).queue();

                            ResponseReaction.remove(message.getIdLong());
                        });

                        this.cancel(message, embed);
                    }
        });
    }

    private void cancel(Message message, EmbedBuilder embed) {
        ResponseReaction.fetch(message.getIdLong())
                .reaction(Bot.INSTANCE.getEmoji("gang_cross"), emoji -> {
            embed.setDescription(Embed.Z + "\nAction has ben cancelled..");
            message.editMessage(embed.build()).queue();

            ResponseReaction.remove(message.getIdLong());
        });
    }
}
