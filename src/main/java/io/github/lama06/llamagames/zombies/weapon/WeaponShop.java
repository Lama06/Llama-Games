package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.util.BlockPosition;

public class WeaponShop {
    public BlockPosition activationBlock;
    public int price;
    public int refillPrice;
    public WeaponType<?> weapon;

    // Gson
    public WeaponShop() { }

    public WeaponShop(BlockPosition activationBlock, int price, int refillPrice, WeaponType<?> weapon) {
        this.activationBlock = activationBlock;
        this.price = price;
        this.refillPrice = refillPrice;
        this.weapon = weapon;
    }
}
