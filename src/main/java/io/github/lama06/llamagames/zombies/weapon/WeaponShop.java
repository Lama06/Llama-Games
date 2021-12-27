package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.util.BlockPosition;

import java.util.Objects;

public class WeaponShop {
    public String name;
    public BlockPosition activationBlock;
    public int price;
    public int refillPrice;
    public WeaponType<?> weapon;

    // Gson
    public WeaponShop() { }

    public WeaponShop(String name, BlockPosition activationBlock, int price, int refillPrice, WeaponType<?> weapon) {
        this.name = name;
        this.activationBlock = activationBlock;
        this.price = price;
        this.refillPrice = refillPrice;
        this.weapon = weapon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeaponShop that = (WeaponShop) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
