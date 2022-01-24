package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.monster.Monster;
import org.bukkit.event.HandlerList;

public class MonsterSpawEvent extends ZombiesEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Monster<?, ?> monster;

    public MonsterSpawEvent(ZombiesGame game, Monster<?, ?> monster) {
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
