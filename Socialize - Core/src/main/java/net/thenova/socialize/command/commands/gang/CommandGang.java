package net.thenova.socialize.command.commands.gang;

import net.thenova.socialize.command.CommandTemplateHelp;
import net.thenova.socialize.command.CommandUsage;
import net.thenova.socialize.command.commands.gang.leader.SubCommandGangDisband;
import net.thenova.socialize.command.commands.gang.leader.join.SubCommandGangClose;
import net.thenova.socialize.command.commands.gang.leader.join.SubCommandGangOpen;
import net.thenova.socialize.command.commands.gang.leader.roles.SubCommandGangDemote;
import net.thenova.socialize.command.commands.gang.leader.roles.SubCommandGangPromote;
import net.thenova.socialize.command.commands.gang.member.SubCommandGangDeposit;
import net.thenova.socialize.command.commands.gang.member.SubCommandGangLeave;
import net.thenova.socialize.command.commands.gang.member.SubCommandGangTax;
import net.thenova.socialize.command.commands.gang.member.SubCommandGangToggle;
import net.thenova.socialize.command.commands.gang.non.SubCommandGangBank;
import net.thenova.socialize.command.commands.gang.non.SubCommandGangCreate;
import net.thenova.socialize.command.commands.gang.non.SubCommandGangInfo;
import net.thenova.socialize.command.commands.gang.non.SubCommandGangJoin;
import net.thenova.socialize.command.commands.gang.officer.SubCommandGangInvite;
import net.thenova.socialize.command.commands.gang.officer.SubCommandGangKick;
import net.thenova.socialize.command.commands.gang.officer.SubCommandGangShop;

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
        usage = "gang <command>",
        description = "Use all the gang commands. "
)
public final class CommandGang extends CommandTemplateHelp {

    public CommandGang() {
        super("gang", "g", "gangs");

        this.addSubCommand(
                new SubCommandGangCreate(),
                new SubCommandGangInvite(),
                new SubCommandGangKick(),
                new SubCommandGangPromote(),
                new SubCommandGangDemote(),
                new SubCommandGangInfo(),
                new SubCommandGangBank(),
                new SubCommandGangTax(),
                new SubCommandGangDeposit(),
                new SubCommandGangShop(),
                new SubCommandGangLeave(),
                new SubCommandGangDisband(),
                new SubCommandGangOpen(),
                new SubCommandGangClose(),
                new SubCommandGangJoin(),
                new SubCommandGangToggle()
        );
    }
}
