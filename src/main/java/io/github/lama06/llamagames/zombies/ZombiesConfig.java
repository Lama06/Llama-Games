package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.zombies.weapon.WeaponShop;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZombiesConfig extends GameConfig {
    public String startArea;
    public Set<Window> windows = new HashSet<>();
    public Set<Door> doors = new HashSet<>();
    public Set<ZombieSpawnLocation> additionalSpawnLocations = new HashSet<>();
    public Set<WeaponShop> weaponShops = new HashSet<>();
    public PowerSwitch powerSwitch;
    public Map<Integer, SpawnRate> spawnRates;

    public ZombiesConfig() { }
}
