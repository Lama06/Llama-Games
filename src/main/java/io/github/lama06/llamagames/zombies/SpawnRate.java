package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.zombie.ZombieType;

import java.util.Map;

public class SpawnRate {
    public Map<ZombieType<?, ?>, Integer> zombies;
    public int delay;

    // Gson
    public SpawnRate() { }
}
