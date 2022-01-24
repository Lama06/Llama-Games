package io.github.lama06.llamagames.zombies.monster;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

public class MeleeAttackPlayerComponent {
    public int damage;
    public double maxAttackRange;
    public int attackCooldown;
    public int remainingAttackCooldown;

    public MeleeAttackPlayerComponent(int damage, double maxAttackRange, int attackCooldown) {
        this.damage = damage;
        this.maxAttackRange = maxAttackRange;
        this.attackCooldown = attackCooldown;
    }

    public static class MeleeAttackPlayerSystem extends MonsterSystem {
        public MeleeAttackPlayerSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (Monster<?, ?> monster : game.getMonsters()) {
                MeleeAttackPlayerComponent component = monster.getComponents().getComponent(MeleeAttackPlayerComponent.class);
                if (component == null) {
                    continue;
                }

                if (component.remainingAttackCooldown > 0) {
                    component.remainingAttackCooldown--;
                    continue;
                }

                if (!(monster.getEntity() instanceof LivingEntity entity)) {
                    continue;
                }

                EntityPosition entityPosition = new EntityPosition(entity.getLocation());
                for (ZombiesPlayer player : game.getZombiesPlayers()) {
                    EntityPosition playerPosition = new EntityPosition(player.getPlayer().getLocation());

                    if (entityPosition.getDistanceTo(playerPosition).sum() <= component.maxAttackRange) {
                        player.damage(component.damage);

                        // The game may have ended because the player may have been killed by the damage.
                        // Continuing may result in a NullPointerException because the value of game.getZombiesPlayers()
                        // will be set to null in the handleGameEnded method of the ZombiesGame class.
                        if (!game.isRunning()) return;

                        component.remainingAttackCooldown = component.attackCooldown;
                        break;
                    }
                }
            }
        }
    }
}
