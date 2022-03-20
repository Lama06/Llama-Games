package io.github.lama06.llamaplugin.games.zombies.monster;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.util.EntityPosition;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class MonsterType<T extends Monster<T, E>, E extends Entity> {
    private static final Set<MonsterType<?, ?>> TYPES = new HashSet<>();

    public static Set<MonsterType<?, ?>> getTypes() {
        return TYPES;
    }

    public static Optional<MonsterType<?, ?>> getByName(String name) {
        return TYPES.stream().filter(type -> type.name.equals(name)).findFirst();
    }

    public static final MonsterType<EasyZombie, Zombie> EASY_ZOMBIE = EasyZombie.TYPE;

    public static final MonsterType<MediumZombie, Zombie> MEDIUM_ZOMBIE = MediumZombie.TYPE;

    public static final MonsterType<HardZombie, Zombie> HARD_ZOMBIE = HardZombie.TYPE;

    private final String name;
    private final MonsterSpawnLocation spawnLocation;
    private final Class<E> entityType;
    private final MonsterCreator<T, E> creator;

    public MonsterType(String name, MonsterSpawnLocation spawnLocation, Class<E> entityType, MonsterCreator<T, E> creator) {
        this.name = name;
        this.spawnLocation = spawnLocation;
        this.entityType = entityType;
        this.creator = creator;

        TYPES.add(this);
    }

    public String getName() {
        return name;
    }

    public MonsterSpawnLocation getSpawnLocation() {
        return spawnLocation;
    }

    public Class<E> getEntityType() {
        return entityType;
    }

    public MonsterCreator<T, E> getCreator() {
        return creator;
    }

    @FunctionalInterface
    public interface MonsterCreator<T extends Monster<T, E>, E extends Entity> {
        T createMonster(ZombiesGame game, MonsterType<T, E> type, World world, EntityPosition position);
    }
}
