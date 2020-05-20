package net.thenova.socialize.command.commands.gang.officer;

import net.dv8tion.jda.api.entities.Member;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.gangs.gang.Gang;
import net.thenova.socialize.gangs.gang.GangRole;

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
        min = 1,
        usage = "gang kick <@user>",
        description = "Kick a user from the gang. (Officer only)"
)
public final class SubCommandGangKick extends CommandTemplateGang {

    public SubCommandGangKick() {
        super("kick", GangRole.OFFICER);
    }

    @Override
    public void run(final Gang gang, final Entity entity, final CommandContext context) {
        final List<Member> mentioned = context.getMessage().getMentionedMembers();

        if (mentioned.isEmpty()) {
            context.error("You must mention at least 1 member.");
            return;
        }

        final Entity kicked = EntityHandler.INSTANCE.getEntity(mentioned.get(0));
        if(kicked == null) {
            GangManager.INSTANCE.log(gang, "[SubCommandGangKick] - Failed to kick mentioned member as EntityHandler returned null");
            return;
        }

        gang.getMembers().kick(context, entity, kicked);
    }
}
