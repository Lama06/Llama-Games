package io.github.lama06.llamagames.zombies.monster;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MonsterSystem implements Listener {
    protected final ZombiesGame game;

    public MonsterSystem(ZombiesGame game) {
        this.game = game;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
