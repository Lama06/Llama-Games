package io.github.lama06.llamagames;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class LlamaGamesPlugin extends JavaPlugin {
    private GameManager gameManager;
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getSLF4JLogger();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        gameManager = new GameManager(this);

        gameManager.backupConfigFile();

        boolean loadResult = gameManager.loadGames();
        if (!loadResult) {
            logger.error("Failed to load the games from the game config file! Disabling the plugin!");
            Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().disablePlugin(this));
            return;
        }

        new LlamaGamesCommand(this, "llamagames");

        for (GameType<?, ?> gameType : GameType.getValues()) {
            Consumer<LlamaGamesPlugin> callback = gameType.getPluginEnableCallback();
            if (callback != null) {
                callback.accept(this);
            }
        }
    }

    @Override
    public void onDisable() {
        boolean saved = gameManager.saveGameConfig();
        if (saved) {
            logger.info("Config file successfully saved");
        } else {
            logger.info("The config file was not saved");
        }

        gameManager.unloadGames();

        for (GameType<?, ?> gameType : GameType.getValues()) {
            Consumer<LlamaGamesPlugin> callback = gameType.getPluginDisableCallback();
            if (callback != null) {
                callback.accept(this);
            }
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
