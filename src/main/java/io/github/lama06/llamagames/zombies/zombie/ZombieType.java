package io.github.lama06.llamagames.zombies.zombie;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class ZombieType<T extends AbstractZombie<T, E>, E extends Entity> {
    private static final Set<ZombieType<?, ?>> TYPES = new HashSet<>();

    public static Set<ZombieType<?, ?>> getTypes() {
        return TYPES;
    }

    private final SpawnType spawnType;
    private final Class<E> entityType;
    private final ZombieCreator<T, E> creator;

    private ZombieType(SpawnType spawnType, Class<E> entityType, ZombieCreator<T, E> creator) {
        this.spawnType = spawnType;
        this.creator = creator;
        this.entityType = entityType;

        TYPES.add(this);
    }

    public SpawnType getSpawnType() {
        return spawnType;
    }

    public Class<E> getEntityType() {
        return entityType;
    }

    public ZombieCreator<T, E> getCreator() {
        return creator;
    }

    public enum SpawnType {
        WINDOW,
        ADDITIONAL_SPAWN_LOCATION
    }

    @FunctionalInterface
    public interface ZombieCreator<T extends AbstractZombie<T, E>, E extends Entity> {
        T createZombie(ZombiesGame game, ZombieType<T, E> type);
    }
}
