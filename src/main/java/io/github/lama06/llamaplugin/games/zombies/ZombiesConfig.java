package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.games.GameConfig;
import io.github.lama06.llamaplugin.util.GsonConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZombiesConfig extends GameConfig {
    public String startArea = null;
    public Set<Door> doors = new HashSet<>();
    public Set<Window> windows = new HashSet<>();
    public Set<WeaponShop> weaponShops = new HashSet<>();
    public Set<AdditionalZombieSpawnLocation> additionalZombieSpawnLocations = new HashSet<>();
    public PowerSwitch powerSwitch = null;
    public Map<Integer, SpawnRate> spawnRates = SpawnRate.DEFAULT_SPAWN_RATE;

    @GsonConstructor
    public ZombiesConfig() { }

    public SpawnRate getSpawnRate(int round) {
        if (spawnRates.containsKey(round)) {
            return spawnRates.get(round);
        }

        if (spawnRates.containsKey(-1)) {
            return spawnRates.get(-1);
        }

        return null;
    }

    @Override
    public boolean isComplete() {
        return super.isComplete() && startArea != null && powerSwitch != null;
    }
}
