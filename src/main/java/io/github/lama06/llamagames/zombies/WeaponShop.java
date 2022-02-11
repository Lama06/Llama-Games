package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.Named;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;

public class WeaponShop implements Named {
    public String name;
    public WeaponType<?> weapon;
    public BlockPosition activationBLock;
    public int gold;
    public int refillPrice;

    // Gson
    public WeaponShop() { }

    public WeaponShop(String name, WeaponType<?> weapon, BlockPosition activationBLock, int gold, int refillPrice) {
        this.name = name;
        this.weapon = weapon;
        this.activationBLock = activationBLock;
        this.gold = gold;
    }

    @Override
    public String getName() {
        return name;
    }
}
