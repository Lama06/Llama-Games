package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.monster.MonsterType;

import java.util.Map;

public class SpawnRate {
    public static final Map<Integer, SpawnRate> DEFAULT_SPAWN_RATE = Map.ofEntries(
            Map.entry(1, new SpawnRate(60, Map.ofEntries(
                    Map.entry(MonsterType.EASY_ZOMBIE, 10)
            )))
    );

    public int delay;
    public Map<MonsterType<?, ?>, Integer> monsters;

    // Gson
    public SpawnRate() { }

    public SpawnRate(int delay, Map<MonsterType<?, ?>, Integer> monsters) {
        this.delay = delay;
        this.monsters = monsters;
    }
}
