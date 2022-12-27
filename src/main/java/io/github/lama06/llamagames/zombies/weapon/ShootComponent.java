package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import io.github.lama06.llamagames.zombies.monster.HealthComponent;
import io.github.lama06.llamagames.zombies.monster.Monster;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShootComponent {
    public int maxRange;
    public int damage;

    public ShootComponent(int maxRange, int damage) {
        this.maxRange = maxRange;
        this.damage = damage;
    }

    public static class ShootSystem extends WeaponSystem {
        public ShootSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void handlePlayerInteractEvent(PlayerInteractEvent event) {
            if (!event.getAction().isLeftClick()) {
                return;
            }

            ZombiesPlayer player = game.getZombiesPlayer(event.getPlayer());
            if (player == null) {
                return;
            }

            Weapon<?> weapon = player.getWeaponInHand();
            if (weapon == null) {
                return;
            }

            AmmoComponent ammoComponent = weapon.getComponents().getComponent(AmmoComponent.class);
            if (ammoComponent != null) {
                if (ammoComponent.remainingReloadTime != 0 || ammoComponent.magazineAmmoRemaining == 0) {
                    return;
                }
                ammoComponent.magazineAmmoRemaining--;
            }

            AttackCooldownComponent cooldownComponent = weapon.getComponents().getComponent(AttackCooldownComponent.class);
            if (cooldownComponent != null) {
                if (!cooldownComponent.canAttack()) {
                    return;
                }
                cooldownComponent.startCooldown();
            }

            ShootComponent shootComponent = weapon.getComponents().getComponent(ShootComponent.class);
            if (shootComponent == null) {
                return;
            }

            Entity targetEntity = player.getPlayer().getTargetEntity(shootComponent.maxRange);
            if (targetEntity == null) {
                return;
            }

            Monster<?, ?> monster = game.getMonster(targetEntity);
            if (monster == null) {
                return;
            }

            HealthComponent healthComponent = monster.getComponents().getComponent(HealthComponent.class);
            if (healthComponent == null) {
                return;
            }

            healthComponent.damage(shootComponent.damage, player.getPlayer());
        }
    }
}
