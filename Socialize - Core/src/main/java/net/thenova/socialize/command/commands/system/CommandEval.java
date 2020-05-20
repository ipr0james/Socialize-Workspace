package net.thenova.socialize.command.commands.system;

import net.thenova.socialize.Bot;
import net.thenova.socialize.command.Command;
import net.thenova.socialize.command.CommandContext;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.command.permission.CommandPermission;
import net.thenova.socialize.entities.Entity;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.games.GameManager;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.leaderboard.LeaderboardManager;
import net.thenova.socialize.level.LevelManager;
import net.thenova.socialize.util.UString;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
public final class CommandEval extends Command {

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    /**
     * Creates the command.
     */
    public CommandEval() {
        super("eval", "evaluate", "js", "justgeekythings");

        this.addPermission(
                CommandPermission.developer()
        );

        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, java.net);");
        } catch(ScriptException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Evaluates the given code.
     * @param context The command context.
     */
    @Override
    protected void execute(Entity entity, CommandContext context) {
        if(entity.getUserID() != Bot.DEVELOPER_ID) {
            context.reply("\uD83D\uDE20");
            return;
        }

        if(context.getArguments().length < 1) {
            //sendUsage(context);
            return;
        }
        String code = UString.concat(context.getArguments(), " ", 0);

        engine.put("c", context);
        engine.put("ctx", context);
        engine.put("context", context);
        engine.put("entity", entity);

        engine.put("command", CommandMap.INSTANCE);
        engine.put("bot", Bot.INSTANCE);
        engine.put("entity", EntityHandler.INSTANCE);
        engine.put("game", GameManager.INSTANCE);
        engine.put("gang", GangManager.INSTANCE);
        engine.put("guild", GuildHandler.INSTANCE);
        engine.put("leaderboard", LeaderboardManager.INSTANCE);
        engine.put("level", LevelManager.INSTANCE);

        Object out;
        try {
            out = engine.eval("(function(){with(imports){" + code + "}})();");
        } catch(ScriptException exception) {
            context.reply("Error, stacktrace printed: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        String response;
        if(out != null) {
            response = UString.stripMassMentions(out.toString());
        } else {
            response = "Execution completed.";
        }

        if(response.length() > 2000) {
            context.getChannel().sendFile(response.getBytes(), "output_past_threshold.txt")
                    .queue(null, error -> context.reply("Well, it seems as if the output can't be sent as a file."));
            return;
        }
        context.reply("**Output:**```" + response + "```");
    }
}
