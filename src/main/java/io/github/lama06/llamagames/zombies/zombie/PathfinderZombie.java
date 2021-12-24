package io.github.lama06.llamagames.zombies.zombie;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public abstract class PathfinderZombie<T extends AbstractZombie<T, E>, E extends Mob> extends AbstractZombie<T, E> {
    protected UUID currentTarget;
    protected NoTargetReason noTargetReason = NoTargetReason.NEVER_SEARCHED;
    protected int noTargetSince = Bukkit.getCurrentTick();

    public PathfinderZombie(ZombiesGame game, ZombieType<T, E> type) {
        super(game, type);
    }

    protected void removeTarget(NoTargetReason reason) {
        currentTarget = null;
        noTargetReason = reason;
        noTargetSince = Bukkit.getCurrentTick();
    }

    protected boolean shouldSearchNewTarget() {
        return switch (noTargetReason) {
            case NEVER_SEARCHED, PLAYER_GONE -> true;
            case CANNOT_FIND_PATH -> Bukkit.getCurrentTick()-noTargetSince > 100;
        };
    }

    protected Player searchNewTarget() {
        Player nearbyPlayer = getNearbyPlayer();
        if (nearbyPlayer == null) {
            return null;
        }

        currentTarget = nearbyPlayer.getUniqueId();
        noTargetReason = null;

        return nearbyPlayer;
    }

    protected boolean shouldSearchPath() {
        if (!entity.getPathfinder().hasPath()) {
            return true;
        }

        return Bukkit.getCurrentTick() % 100 == 0;
    }

    protected abstract boolean canAttack(Player player);

    protected abstract void attack(Player player);

    @EventHandler
    public void tickPathfinder(ServerTickStartEvent event) {
        if (currentTarget != null && !game.getPlayerUUUIDs().contains(currentTarget)) {
            removeTarget(NoTargetReason.PLAYER_GONE);
        }

        Player target;
        if (currentTarget == null) {
            if (shouldSearchNewTarget()) {
                target = searchNewTarget();
            } else {
                return;
            }
        } else {
            target = Bukkit.getPlayer(currentTarget);
            if (target == null)
                return;
        }

        if (canAttack(target)) {
            attack(target);
            return;
        }

        if (shouldSearchPath()) {
            Player player = Bukkit.getPlayer(currentTarget);
            if (player == null) {
                return;
            }

            boolean success = entity.getPathfinder().moveTo(player.getLocation());

            if (!success) {
                removeTarget(NoTargetReason.CANNOT_FIND_PATH);
            }
        }
    }

    protected enum NoTargetReason {
        NEVER_SEARCHED,
        CANNOT_FIND_PATH,
        PLAYER_GONE
    }
}
