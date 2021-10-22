package io.github.lama06.lamagames;

import org.bukkit.World;
import org.bukkit.event.Listener;

public abstract class Game<G extends Game<G, C>, C> implements Listener {
    protected LamaGamesPlugin plugin;
    protected GameType<G, C> type;
    protected World world;
    protected C config;
    protected boolean running;

    public Game(LamaGamesPlugin plugin, World world, C config, GameType<G, C> type) {
        this.plugin = plugin;
        this.type = type;
        this.world = world;
        this.config = config;
    }

    public void handleGameLoaded() { }

    public void handleGameUnloaded() { }

    public abstract void handleGameStarted();

    public abstract void handleGameEnded();

    public LamaGamesPlugin getPlugin() {
        return plugin;
    }

    public GameType<G, C> getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }

    public C getConfig() {
        return config;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
