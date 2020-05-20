package net.thenova.socialize.gangs.gang;

import net.thenova.socialize.Bot;
import net.thenova.socialize.Embed;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.modules.EntityBalance;
import net.thenova.socialize.gangs.gang.upgrades.GangUpgradeType;
import net.thenova.socialize.gangs.gang.upgrades.upgrades.GangUpgradeBank;
import net.thenova.titan.library.util.UNumber;

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
public final class GangBank {

    private final Gang gang;
    private final GangData data;

    GangBank(final Gang gang) {
        this.gang = gang;
        this.data = gang.getData();
    }

    public void add(final long value) {
        final long current = this.fetch();
        this.data.set(GangData.Type.BALANCE, Math.min(this.fetch() + value, ((GangUpgradeBank) this.gang.getUpgrades().fetch(GangUpgradeType.BANK)).getMaxBalance()));
    }

    public final void take(final long value) {
        this.data.set(GangData.Type.BALANCE, this.fetch() - value);
    }

    public final long fetch() {
        return (long) data.fetch(GangData.Type.BALANCE);
    }

    public final void deposit(final CommandContext context, final Entity entity) {
        final String arg = context.getArgument(0);
        final long value;
        if(arg.equalsIgnoreCase("all")) {
            value = entity.getBalance().fetch();
        } else {
            if (!UNumber.isLong(arg)) {
                context.error("Invalid amount provided.");
                return;
            }

            value = Long.parseLong(arg);
        }

        if(value < 1) {
            context.error("You cannot deposit `0` " + Bot.INSTANCE.getEmoji("coins") + " to the gang.");
            return;
        }

        if(entity.getBalance().fetch() < value) {
            context.error("You don't have enough coins to deposit that much.");
            return;
        }

        final long current = this.fetch();

        if(current + value > ((GangUpgradeBank) this.gang.getUpgrades().fetch(GangUpgradeType.BANK)).getMaxBalance()) {
            context.error("You cannot deposit this much as it will exceed your banks max balance");
            return;
        }

        if(current < 0 && current + value > 0) {
            this.gang.getData().set(GangData.Type.OVERDRAWN, 0);
        }

        entity.getBalance().take(value, EntityBalance.Reason.SYSTEM);
        this.add(value);

        context.reply(Embed.gang(context.getMember(), "You have added `" + value + "`" + Bot.INSTANCE.getEmoji("coins") + " to your gangs account.")).queue();
    }
}
