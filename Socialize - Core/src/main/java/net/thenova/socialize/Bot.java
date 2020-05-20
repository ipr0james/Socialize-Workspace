package net.thenova.socialize;

import de.arraying.kotys.JSON;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.thenova.socialize.command.CommandMap;
import net.thenova.socialize.database.tables.entities.DBTableEntityData;
import net.thenova.socialize.database.tables.entities.DBTableEntityStats;
import net.thenova.socialize.database.tables.games.DBTableGameLottery;
import net.thenova.socialize.database.tables.gang.DBTableGangData;
import net.thenova.socialize.database.tables.gang.DBTableGangGangs;
import net.thenova.socialize.database.tables.gang.DBTableGangMembers;
import net.thenova.socialize.database.tables.gang.DBTableGangUpgrades;
import net.thenova.socialize.database.tables.guild.DBTableGuildConfiguration;
import net.thenova.socialize.database.tables.guild.DBTableGuildData;
import net.thenova.socialize.entities.EntityHandler;
import net.thenova.socialize.games.GameManager;
import net.thenova.socialize.gangs.GangManager;
import net.thenova.socialize.guild.GuildHandler;
import net.thenova.socialize.leaderboard.LeaderboardManager;
import net.thenova.socialize.level.LevelManager;
import net.thenova.socialize.listeners.*;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.DatabaseHandler;
import net.thenova.titan.library.file.config.ConfigHandler;
import net.thenova.titan.library.util.UNumber;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Bot {
    INSTANCE;

    public static final long DEVELOPER_ID = 483344453407342592L;

    @Getter private static JDA jDA;

    private final Map<String, String> emojis = new HashMap<>();

    private boolean shutdown = false;

    public static void main(String[] args) {
        Thread.currentThread().setName("Main");

        Bot.INSTANCE.load();
    }

    private void load() {
        final Logger logger = Titan.INSTANCE.getLogger();
        logger.info("[Bot] - Socialize Core starting...");
        Titan.INSTANCE.load(() -> "");
        ConfigHandler.INSTANCE.load();

        logger.info("[Bot] - Database table loading starting...");
        DatabaseHandler.INSTANCE.loadTables(
                Arrays.asList(
                        // Guild Data
                        new DBTableGuildData(),
                        new DBTableGuildConfiguration(),
                        // Games
                        new DBTableGameLottery(),
                        // Entity Data
                        new DBTableEntityData(),
                        new DBTableEntityStats(),
                        // Gang Data
                        new DBTableGangGangs(),
                        new DBTableGangData(),
                        new DBTableGangMembers(),
                        new DBTableGangUpgrades()
                )
        );

        logger.info("[Bot] - Emojis loading");
        ConfigHandler.INSTANCE.get("emoji", JSON.class).raw().forEach((key, val) -> this.emojis.put(key, val.toString()));
        this.emojis.put("tick", "✅");
        this.emojis.put("cross", "❌");

        logger.info("[Bot] - Registering commands...");
        CommandMap.INSTANCE.load();

        logger.info("[Bot] - Registering guilds handler...");
        GuildHandler.INSTANCE.load();

        logger.info("[Bot] - Loading JDA...");
        try {
            jDA = new JDABuilder(AccountType.BOT)
                    .setToken(ConfigHandler.INSTANCE.get("bot.token", String.class))
                    .setStatus(OnlineStatus.ONLINE)
                    .build()
                    .awaitReady();
        } catch (LoginException | InterruptedException ex) {
            logger.info("[Bot] - Login failed for JDA", ex);
        }

        logger.info("[Bot] - Registering level manager...");
        LevelManager.INSTANCE.load();

        logger.info("[Bot] - Registering entity handling...");
        EntityHandler.INSTANCE.load();

        logger.info("[Bot] - Registering gangs...");
        GangManager.INSTANCE.load();

        logger.info("[Bot] - Registering leaderboards...");
        LeaderboardManager.INSTANCE.load();

        logger.info("[Bot] - Registering games...");
        GameManager.INSTANCE.load();

        logger.info("[Bot] - Starting guild tasks/actions..");
        GuildHandler.INSTANCE.start();

        logger.info("[Bot] - Registering JDA listeners...");
        jDA.addEventListener(new GuildListener(),
                new MessageListener(),
                new ReactionListener(),
                new RoleListener(),
                new VoiceListener());

        Runtime.getRuntime().addShutdownHook(
                new Thread(Bot.INSTANCE::shutdown)
        );

        logger.info("[Bot] - Loading has completed.");
    }

    public void shutdown() {
        if(this.shutdown) {
            return;
        }

        this.shutdown = true;
        jDA.getRegisteredListeners().forEach(listener -> jDA.removeEventListener(listener));

        LevelManager.INSTANCE.shutdown();
        Titan.INSTANCE.shutdown();
    }

    /**
     * Retrieve an emoji from config
     * @param name - Name
     * @return - Return String emoji
     */
    public String getEmoji(String name) {
        final String emoji = emojis.get(name);
        return UNumber.isLong(emoji) ? "<:" + name + ":" + emoji + ">" : emoji;
    }
}
