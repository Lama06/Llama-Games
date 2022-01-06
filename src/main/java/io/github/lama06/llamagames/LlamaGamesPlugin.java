package io.github.lama06.llamagames;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class LlamaGamesPlugin extends JavaPlugin {
    private GameManager gameManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        gameManager = new GameManager(this);
        gameManager.loadGames();

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
        try {
            gameManager.saveGameConfig();
        } catch (GameManager.GamesSaveFailedException e) {
            e.printStackTrace();
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
