package io.github.lama06.llamaplugin.games.zombies.monster;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unused")
public record MonsterSystemType<T extends MonsterSystem>(Function<ZombiesGame, T> creator) {
    private static final Set<MonsterSystemType<?>> TYPES = new HashSet<>();

    public static Set<MonsterSystemType<?>> getTypes() {
        return TYPES;
    }

    public static MonsterSystemType<MeleeAttackPlayerComponent.MeleeAttackPlayerSystem> MELEE_ATTACK_PLAYER = new MonsterSystemType<>(
            MeleeAttackPlayerComponent.MeleeAttackPlayerSystem::new
    );

    public static MonsterSystemType<HealthComponent.RemoveDeadZombiesSystem> REMOVE_DEAD_ZOMBIES = new MonsterSystemType<>(
            HealthComponent.RemoveDeadZombiesSystem::new
    );

    public static MonsterSystemType<PathfinderComponent.PathfinderSystem> PATHFINDER = new MonsterSystemType<>(
            PathfinderComponent.PathfinderSystem::new
    );

    public MonsterSystemType {
        TYPES.add(this);
    }
}
