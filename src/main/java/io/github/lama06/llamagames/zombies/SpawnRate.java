package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.GsonConstructor;
import io.github.lama06.llamagames.zombies.monster.MonsterType;

import java.util.Map;

public class SpawnRate {
    public static final Map<Integer, SpawnRate> DEFAULT_SPAWN_RATE = Map.ofEntries(
            Map.entry(1, new SpawnRate(80, Map.ofEntries(
                    Map.entry(MonsterType.EASY_ZOMBIE, 10)
            ))),
            Map.entry(2, new SpawnRate(80, Map.ofEntries(
                    Map.entry(MonsterType.MEDIUM_ZOMBIE, 10)
            ))),
            Map.entry(3, new SpawnRate(80, Map.ofEntries(
                    Map.entry(MonsterType.HARD_ZOMBIE, 10)
            )))
    );

    public int delay;
    public Map<MonsterType<?, ?>, Integer> monsters;

    @GsonConstructor
    public SpawnRate() { }

    public SpawnRate(int delay, Map<MonsterType<?, ?>, Integer> monsters) {
        this.delay = delay;
        this.monsters = monsters;
    }
}
