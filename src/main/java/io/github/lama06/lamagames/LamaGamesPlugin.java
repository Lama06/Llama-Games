package io.github.lama06.lamagames;

import org.bukkit.plugin.java.JavaPlugin;

public class LamaGamesPlugin extends JavaPlugin {
    private GameManager gameManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        gameManager = new GameManager(this);
        try {
            gameManager.loadGames();
        } catch (GameManager.GamesLoadFailedException e) {
            e.printStackTrace();
            return;
        }

        new LamaGamesCommand(this, "lamagames");
    }

    @Override
    public void onDisable() {
        try {
            gameManager.saveGames();
        } catch (GameManager.GamesSaveFailedException e) {
            e.printStackTrace();
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
