package io.github.lama06.llamagames.zombies.monster;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.Set;

public class HealthComponent {
    public int health;
    public Player lastDamageSource;

    public HealthComponent(int initialHealth) {
        this.health = initialHealth;
    }

    public void damage(int amount) {
        health -= amount;
    }

    public void damage(int amount, Player lastDamageSource) {
        damage(amount);
        this.lastDamageSource = lastDamageSource;
    }

    public static class RemoveDeadZombiesSystem extends MonsterSystem {
        public RemoveDeadZombiesSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            record DeadMonsterData(Monster<?, ?> monster, ZombiesPlayer killedBy) { }

            if (!game.isRunning()) return;

            Set<DeadMonsterData> deadMonsters = new HashSet<>(); // Avoid ConcurrentModificationException

            for (Monster<?, ?> monster : game.getMonsters()) {
                HealthComponent component = monster.getComponents().getComponent(HealthComponent.class);
                if (component == null) {
                    continue;
                }

                ZombiesPlayer killer = null;
                if (component.lastDamageSource != null) {
                    killer = game.getZombiesPlayer(component.lastDamageSource);
                }

                if (component.health <= 0) {
                    deadMonsters.add(new DeadMonsterData(monster, killer));
                }
            }

            for (DeadMonsterData deadMonster : deadMonsters) {
                game.handleMonsterDied(deadMonster.monster, deadMonster.killedBy);
            }
        }
    }
}
