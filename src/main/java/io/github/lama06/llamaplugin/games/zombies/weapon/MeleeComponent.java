package io.github.lama06.llamaplugin.games.zombies.weapon;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.games.zombies.ZombiesPlayer;
import io.github.lama06.llamaplugin.games.zombies.monster.HealthComponent;
import io.github.lama06.llamaplugin.games.zombies.monster.Monster;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class MeleeComponent {
    public int maxRange;
    public int damage;

    public MeleeComponent(int maxRange, int damage) {
        this.maxRange = maxRange;
        this.damage = damage;
    }

    public static class MeleeAttackSystem extends WeaponSystem {
        public MeleeAttackSystem(ZombiesGame game) {
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

            MeleeComponent meleeComponent = weapon.getComponents().getComponent(MeleeComponent.class);
            if (meleeComponent == null) {
                return;
            }

            AttackCooldownComponent cooldownComponent = weapon.getComponents().getComponent(AttackCooldownComponent.class);
            if (cooldownComponent != null) {
                if (!cooldownComponent.canAttack()) {
                    return;
                }
            }

            Entity targetEntity = player.getPlayer().getTargetEntity(meleeComponent.maxRange);
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

            healthComponent.damage(meleeComponent.damage, player.getPlayer());

            if (cooldownComponent != null) {
                cooldownComponent.startCooldown();
            }
        }
    }
}
