package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;

public class Rifle extends ShootingWeapon<Rifle> {
    public Rifle(ZombiesGame game, ZombiesPlayer player, WeaponType<Rifle> type) {
        super(game, player, type);
    }

    @Override
    public int getMaxAmmo() {
        return 300;
    }

    @Override
    public int getMaxMagazineAmmo() {
        return 30;
    }

    @Override
    public int getAmmoRefillTime() {
        return 40;
    }

    @Override
    public int getCooldownAfterUse() {
        return 4;
    }

    @Override
    public int getDamage() {
        return 1;
    }

    @Override
    public int getMaxRange() {
        return 30;
    }
}
