package io.github.lama06.llamagames.zombies.monster;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PathfinderComponent {
    public Player currentTarget;

    public static class PathfinderSystem extends MonsterSystem {
        public PathfinderSystem(ZombiesGame game) {
            super(game);
        }

        private static Player searchNearestTarget(Monster<?, ?> monster, ZombiesGame game) {
            return game.getPlayers().stream().min((p1, p2) -> {
                EntityPosition position = new EntityPosition(monster.getEntity().getLocation());
                EntityPosition.Distance distance1 = new EntityPosition(p1.getLocation()).getDistanceTo(position);
                EntityPosition.Distance distance2 = new EntityPosition(p2.getLocation()).getDistanceTo(position);
                return distance1.compareTo(distance2);
            }).orElse(null);
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

                if (component.currentTarget == null) {
                    Player newTarget = searchNearestTarget(monster, game);
                    if (newTarget == null) {
                        continue;
                    }
                    component.currentTarget = newTarget;
                }

                entity.lookAt(component.currentTarget);

                if (!entity.getPathfinder().hasPath()) {
                    boolean foundPath = entity.getPathfinder().moveTo(entity);
                    if (!foundPath) {
                        component.currentTarget = null;
                    }
                }
            }
        }
    }
}
