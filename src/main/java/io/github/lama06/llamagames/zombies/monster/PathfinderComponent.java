package io.github.lama06.llamagames.zombies.monster;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.MonsterSpawnEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Optional;

public class PathfinderComponent {
    public static class PathfinderSystem extends MonsterSystem {
        public PathfinderSystem(ZombiesGame game) {
            super(game);
        }

        private Optional<Player> searchNearestTarget(Monster<?, ?> monster) {
            return game.getPlayers().stream()
                    .min((p1, p2) -> {
                        EntityPosition position = new EntityPosition(monster.getEntity().getLocation());
                        EntityPosition.Distance distance1 = new EntityPosition(p1.getLocation()).getDistanceTo(position);
                        EntityPosition.Distance distance2 = new EntityPosition(p2.getLocation()).getDistanceTo(position);
                        return distance1.compareTo(distance2);
                    });
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (Monster<?, ?> monster : game.getMonsters()) {
                PathfinderComponent component = monster.getComponents().getComponent(PathfinderComponent.class);
                if (component == null) {
                    continue;
                }

                if (!(monster.getEntity() instanceof Mob entity)) {
                    continue;
                }

                Optional<Player> player = searchNearestTarget(monster);
                if (player.isEmpty()) {
                    return;
                }

                entity.getPathfinder().moveTo(player.get());
            }
        }

        @EventHandler
        public void removeVanillaGoals(MonsterSpawnEvent event) {
            if (!event.getGame().equals(game)) {
                return;
            }

            Monster<?, ?> monster = event.getMonster();

            if (!monster.getComponents().hasComponent(PathfinderComponent.class)) {
                return;
            }

            Entity entity = monster.getEntity();
            if (!(entity instanceof Mob mob)) {
                return;
            }

            Bukkit.getMobGoals().removeAllGoals(mob);
        }
    }
}
