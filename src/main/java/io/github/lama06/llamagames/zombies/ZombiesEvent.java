package io.github.lama06.llamagames.zombies;

import org.bukkit.event.Event;

public abstract class ZombiesEvent extends Event {
    private final ZombiesGame game;

    public ZombiesEvent(ZombiesGame game) {
        this.game = game;
    }

    public ZombiesGame getGame() {
        return game;
    }
}
