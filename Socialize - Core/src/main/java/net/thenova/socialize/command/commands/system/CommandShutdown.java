package net.thenova.socialize.command.commands.system;

import net.thenova.socialize.Bot;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;

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
public final class CommandShutdown extends Command {

    public CommandShutdown() {
        super("shutdown");

        this.addPermission(
                CommandPermission.developer()
        );
    }

    @Override
    protected void execute(Entity entity, CommandContext context) {
        if(entity.getUserID() != Bot.DEVELOPER_ID) {
            context.reply("\uD83D\uDE20");
            return;
        }

        Bot.INSTANCE.shutdown();
        context.getMessage()
                .addReaction(Bot.INSTANCE.getEmoji("tick"))
                .queue(v -> this.handle(), ex -> this.handle());
    }

    private void handle() {
        System.exit(0);
    }
}
