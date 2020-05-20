package net.thenova.socialize.command.commands.gang.non;

import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.entities.modules.stat_keys.gang.GangID;
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
        usage = "gang join <name/user>",
        description = "Allows you to join a gang that is currently open"
)
public final class SubCommandGangJoin extends CommandTemplateGang {

    public SubCommandGangJoin() {
        super("join", GangRole.NON);
    }

    @Override
    public final void run(Gang gang, final Entity entity, final CommandContext context) {
        final long currentGangID = entity.getStats().fetch(new GangID());
        if(currentGangID != -1L) {
            context.error("You are currently part of a gang and cannot join another.");
            return;
        }

        if(context.getMessage().getMentionedMembers().isEmpty()) {
            final String name = String.join(" ", context.getArguments());
            gang = GangManager.INSTANCE.getGangByName(entity.getGuildID(), name);

            if (gang == null) {
                context.error("**" + name + "** is not a gang.");
                return;
            }
        } else {
            final Entity other = EntityHandler.INSTANCE.getEntity(context.getMessage().getMentionedMembers().get(0));

            long gangID;
            if(other == null || (gangID = other.getStats().fetch(new GangID())) == -1) {
                context.error("That member is not a part of a gang.");
                return;
            }

            gang = GangManager.INSTANCE.getGang(entity.getGuildID(), gangID);
        }

        gang.getMembers().inviteJoin(context, entity);
    }
}
