package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.games.zombies.monster.Monster;
import org.bukkit.event.HandlerList;

public class MonsterSpawnEvent extends ZombiesEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Monster<?, ?> monster;

    public MonsterSpawnEvent(ZombiesGame game, Monster<?, ?> monster) {
        super(game);
        this.monster = monster;
    }

    public Monster<?, ?> getMonster() {
        return monster;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
