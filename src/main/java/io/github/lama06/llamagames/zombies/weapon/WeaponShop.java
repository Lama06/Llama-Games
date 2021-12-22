package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.util.BlockPosition;

public class WeaponShop {
    public BlockPosition activationBlock;
    public int gold;
    public WeaponType<?> weapon;

    // Gson
    public WeaponShop() { }

    public WeaponShop(BlockPosition activationBlock, int gold, WeaponType<?> weapon) {
        this.activationBlock = activationBlock;
        this.gold = gold;
        this.weapon = weapon;
    }
}
