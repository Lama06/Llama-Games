package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;

public class Shotgun extends ShootingWeapon<Shotgun> {
    public Shotgun(ZombiesGame game, ZombiesPlayer player, WeaponType<Shotgun> type) {
        super(game, player, type);
    }

    @Override
    public int getMaxAmmo() {
        return 80;
    }

    @Override
    public int getMaxMagazineAmmo() {
        return 10;
    }

    @Override
    public int getAmmoRefillTime() {
        return 60;
    }

    @Override
    public int getCooldownAfterUse() {
        return 20;
    }

    @Override
    public int getDamage() {
        return 20;
    }

    @Override
    public int getMaxRange() {
        return 6;
    }
}
