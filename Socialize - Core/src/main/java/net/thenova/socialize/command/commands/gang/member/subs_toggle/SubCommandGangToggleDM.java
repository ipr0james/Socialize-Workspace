package net.thenova.socialize.command.commands.gang.member.subs_toggle;

import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.CommandTemplateGang;
import net.thenova.socialize.entities.Entity;
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
        min = 0,
        usage = "gang toggle dms",
        description = "Toggle on/off dms from the Bot about your gang."
)
public final class SubCommandGangToggleDM extends CommandTemplateGang {

    public SubCommandGangToggleDM() {
        super("dms", GangRole.MEMBER);
    }

    @Override
    public void run(final Gang gang, final Entity entity, final CommandContext context) {
        gang.getMembers().fetch(entity.getUserID()).toggleDMs(entity, context);
    }
}

