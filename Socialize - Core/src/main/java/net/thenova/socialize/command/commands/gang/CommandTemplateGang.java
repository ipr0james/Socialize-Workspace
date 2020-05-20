package net.thenova.socialize.command.commands.gang;

import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.entities.Entity;
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
public abstract class CommandTemplateGang extends Command {

    private final GangRole role;

    public CommandTemplateGang(String name, GangRole role, String... aliases) {
        super(name,  aliases);

        this.role = role;
    }

    @Override
    protected void execute(final Entity entity, final CommandContext context) {
        final long gangID = entity.getStats().fetch(new GangID());
        if(gangID == -1) {
            if(this.role == GangRole.NON) {
                this.run(null, entity, context);
            } else {
                context.error("You must be the member of a gang to execute this command.");
            }
            return;
        }

        final Gang gang = GangManager.INSTANCE.getGang(context.getGuild().getIdLong(), gangID);
        if(!this.role.hasRole(gang.getMembers().fetch(entity.getUserID()).getRole())) {
            context.error("You must be " + this.role.context() + " to use this command.");
            return;
        }

        this.run(gang, entity, context);
    }

    public abstract void run(final Gang gang, final Entity entity, final CommandContext context);
}
