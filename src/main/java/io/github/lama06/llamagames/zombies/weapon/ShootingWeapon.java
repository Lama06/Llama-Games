package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.util.Pair;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import io.github.lama06.llamagames.zombies.zombie.AbstractZombie;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class ShootingWeapon<T extends ShootingWeapon<T>> extends AmmoWeapon<T> {
    public ShootingWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type) {
        super(game, player, type);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        super.onUse(event);

        Entity targetEntity = event.getPlayer().getTargetEntity(getMaxRange());
        if (targetEntity == null) {
            return;
        }

        AbstractZombie<?, ?> zombie = game.getZombie(targetEntity);
        if (zombie == null) {
            return;
        }
        zombie.damage(getDamage());
    }

    public abstract int getDamage();

    public abstract int getMaxRange();

    public abstract Pair<Particle, Object> getParticle();
}
