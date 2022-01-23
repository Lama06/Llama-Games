package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;

import java.util.Objects;

public class WeaponShop {
    public String name;
    public WeaponType<?> weapon;
    public BlockPosition activationBLock;
    public int gold;

    // Gson
    public WeaponShop() { }

    public WeaponShop(String name, WeaponType<?> weapon, BlockPosition activationBLock, int gold) {
        this.name = name;
        this.weapon = weapon;
        this.activationBLock = activationBLock;
        this.gold = gold;
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
