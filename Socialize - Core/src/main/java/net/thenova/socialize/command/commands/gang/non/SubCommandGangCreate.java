package net.thenova.socialize.command.commands.gang.non;

import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangRole;

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
        min = 1,
        usage = "gang create <name>",
        description = "Create a gang."
)
public final class SubCommandGangCreate extends CommandTemplateGang {

    public SubCommandGangCreate() {
        super("create", GangRole.NON);
    }

    @Override
    public void run(Gang gang, Entity entity, CommandContext context) {
        GangManager.INSTANCE.createGang(entity, context);
    }
}
