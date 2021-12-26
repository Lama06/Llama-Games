package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import io.github.lama06.llamagames.zombies.zombie.AbstractZombie;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class MeleeWeapon extends CooldownWeapon<MeleeWeapon> {
    public MeleeWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<MeleeWeapon> type) {
        super(game, player, type);
    }

    public abstract int getMaxRange();

    public abstract int getDamage();

    @Override
    public void onUse(PlayerInteractEvent event) {
        super.onUse(event);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }

        Entity targetEntity = player.getPlayer().getTargetEntity(getMaxRange());
        if (targetEntity == null) {
            return false;
        }

        AbstractZombie<?, ?> zombie = game.getZombie(targetEntity);
        return zombie != null;
    }
}
