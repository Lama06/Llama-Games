package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import io.github.lama06.llamagames.zombies.zombie.AbstractZombie;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

public class Knife extends CooldownWeapon<Knife> {
    public Knife(ZombiesGame game, ZombiesPlayer player, WeaponType<Knife> type) {
        super(game, player, type);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        Entity targetEntity = event.getPlayer().getTargetEntity(4);
        if (targetEntity == null) {
            return;
        }

        AbstractZombie<?, ?> zombie = game.getZombie(targetEntity);
        if (zombie == null) {
            return;
        }
        zombie.damage(3);
    }

    @Override
    public int getCooldownAfterUse() {
        return 20;
    }
}
