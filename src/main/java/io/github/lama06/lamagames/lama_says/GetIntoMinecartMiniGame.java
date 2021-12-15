package io.github.lama06.lamagames.lama_says;

import io.github.lama06.lamagames.util.Area;
import io.github.lama06.lamagames.util.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class GetIntoMinecartMiniGame extends CompleteMiniGame<GetIntoMinecartMiniGame> {
    public GetIntoMinecartMiniGame(LamaSaysGame game, Consumer<GetIntoMinecartMiniGame> callback) {
        super(game, callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Get into a minecart");
    }

    @Override
    public void handleGameStarted() {
        int numberOfMinecarts = getNumberOfMinecarts();
        Area floor = game.getConfig().floor;
        Set<BlockPosition> minecartPositions = new HashSet<>();

        for (int i = 1; i <= numberOfMinecarts; i++) {
            BlockPosition position;
            while (true) {
                BlockPosition randomBlockPosition = new BlockPosition(
                        game.getRandom().nextInt(floor.getLowerX(), floor.getUpperX()+1),
                        game.getRandom().nextInt(floor.getLowerY(), floor.getUpperY()+1),
                        game.getRandom().nextInt(floor.getLowerZ(), floor.getUpperZ()+1)
                );

                if (minecartPositions.contains(randomBlockPosition)) {
                    continue;
                }

                position = randomBlockPosition;
                minecartPositions.add(position);
                break;
            }

            game.getWorld().spawnEntity(position.asLocation(game.getWorld()).add(0, 1, 0), EntityType.MINECART);
        }
    }

    @Override
    public void handleGameEnded() {
        for (RideableMinecart minecart : game.getWorld().getEntitiesByClass(RideableMinecart.class)) {
            minecart.remove();
        }
    }

    @EventHandler
    public void handleVehicleEnterEvent(VehicleEnterEvent event) {
        if (!event.getVehicle().getWorld().equals(game.getWorld()) ||
                ! (event.getVehicle() instanceof RideableMinecart) ||
                !(event.getEntered() instanceof Player player)
        ) {
            return;
        }

        addSuccessfulPlayer(player);
    }

    @EventHandler
    public void handleVehicleExitEvent(VehicleExitEvent event) {
        if (!event.getVehicle().getWorld().equals(game.getWorld()) || !(event.getExited() instanceof Player player)) {
            return;
        }

        event.setCancelled(true);
    }

    private int getNumberOfMinecarts() {
        int players = game.getPlayers().size();

        return players == 1 ? 1 : players-1;
    }
}
