package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.event.EventHandler;

public class AttackCooldownComponent {
    public int attackCooldown;
    public int remainingAttackCooldown;

    public boolean canAttack() {
        return remainingAttackCooldown == 0;
    }

    public void startCooldown() {
        remainingAttackCooldown = attackCooldown;
    }

    public AttackCooldownComponent(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public static class TickAttackCooldownSystem extends WeaponSystem {
        public TickAttackCooldownSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (Weapon<?> weapon : getAllWeapons()) {
                AttackCooldownComponent component = weapon.getComponents().getComponent(AttackCooldownComponent.class);
                if (component == null) {
                    continue;
                }

                if (component.remainingAttackCooldown > 0) {
                    component.remainingAttackCooldown--;
                }
            }
        }
    }
}
