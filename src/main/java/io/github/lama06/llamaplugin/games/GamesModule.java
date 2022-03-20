package io.github.lama06.llamaplugin.games;

import io.github.lama06.llamaplugin.LlamaPlugin;
import io.github.lama06.llamaplugin.Module;
import io.github.lama06.llamaplugin.ModuleEnableFailedException;
import io.github.lama06.llamaplugin.ModuleType;

import java.util.function.Consumer;

public class GamesModule extends Module<GamesModule> {
    private GameManager gameManager;

    public GamesModule(LlamaPlugin plugin, ModuleType<GamesModule> type) {
        super(plugin, type);
    }

    @Override
    public void onEnable() throws ModuleEnableFailedException {
        gameManager = new GameManager(this);

        gameManager.backupConfigFile();

        boolean loadResult = gameManager.loadGames();
        if (!loadResult) {
            throw new ModuleEnableFailedException("Failed to load the games from the game config file!");
        }

        new GamesCommand(this);

        for (GameType<?, ?> gameType : GameType.getValues()) {
            Consumer<GamesModule> callback = gameType.getPluginEnableCallback();
            if (callback != null) {
                callback.accept(this);
            }
        }
    }

    @Override
    public void onDisable() {
        boolean saved = gameManager.saveGameConfig();
        if (saved) {
            plugin.getSLF4JLogger().info("Config file successfully saved");
        } else {
            plugin.getSLF4JLogger().info("The config file was not saved");
        }

        gameManager.unloadGames();

        for (GameType<?, ?> gameType : GameType.getValues()) {
            Consumer<GamesModule> callback = gameType.getPluginDisableCallback();
            if (callback != null) {
                callback.accept(this);
            }
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public LlamaPlugin getPlugin() {
        return plugin;
    }
}
