package io.github.lama06.llamagames.zombies.monster;

import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.util.ComponentContainer;
import io.github.lama06.llamagames.zombies.MonsterSpawnEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public abstract class Monster<T extends Monster<T, E>, E extends Entity> {
    protected final ZombiesGame game;
    protected final ComponentContainer components = new ComponentContainer();
    protected final MonsterType<T, E> type;
    protected E entity;

    public Monster(ZombiesGame game, MonsterType<T, E> type, World world, EntityPosition position) {
        this.game = game;
        this.type = type;

        initComponents();

        entity = world.spawn(position.asLocation(world), type.getEntityType());

        Bukkit.getPluginManager().callEvent(new MonsterSpawnEvent(game, this));
        onSpawned();
    }

    public abstract void initComponents();

    public void onSpawned() { }

    public void remove() {
        entity.remove();
    }

    public ComponentContainer getComponents() {
        return components;
    }

    public ZombiesGame getGame() {
        return game;
    }

    public E getEntity() {
        return entity;
    }
}
