package io.github.lama06.llamagames.zombies.zombie;

import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class AbstractZombie<T extends AbstractZombie<T, E>, E extends Entity> implements Listener {
    protected ZombiesGame game;
    protected ZombieType<T, E> type;
    protected E entity;
    protected int health;

    public AbstractZombie(ZombiesGame game, ZombieType<T, E> type) {
        this.game = game;
        this.type = type;
        health = getInitialHealth();

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public void spawn(EntityPosition position) {
        entity = game.getWorld().spawn(position.asLocation(game.getWorld()), type.getEntityType(), this::onSpawn);
    }

    protected abstract void onSpawn(E entity);

    protected abstract int getInitialHealth();

    public void onRemove(ZombieRemoveReason reason) {
        HandlerList.unregisterAll(this);
    }

    public void remove(ZombieRemoveReason reason) {
        game.removeZombie(this, reason);
    }

    public void setHealth(int health) {
        if (health <= 0) {
            remove(ZombieRemoveReason.DEATH);
        }

        this.health = health;
    }

    public void damage(int damage) {
        setHealth(health - damage);
    }

    protected Player getNearbyPlayer() {
        return game.getPlayers().stream().min((p1, p2) -> {
            EntityPosition position = new EntityPosition(entity.getLocation());

            EntityPosition.Distance distance1 = new EntityPosition(p1.getLocation()).getDistanceTo(position);
            EntityPosition.Distance distance2 = new EntityPosition(p2.getLocation()).getDistanceTo(position);

            return distance1.compareTo(distance2);
        }).orElse(null);
    }

    public E getEntity() {
        return entity;
    }

    public enum ZombieRemoveReason {
        DEATH,
        GAME_UNLOAD
    }
}
