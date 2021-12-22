package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.zombies.weapon.WeaponShop;

import java.util.Map;
import java.util.Set;

public class ZombiesConfig extends GameConfig {
    public String startArea;
    public Set<Window> windows;
    public Set<Door> doors;
    public Set<ZombieSpawnLocation> additionalSpawnLocations;
    public Set<WeaponShop> weaponShops;
    public PowerSwitch powerSwitch;
    public Map<Integer, SpawnRate> spawnRates;
}
