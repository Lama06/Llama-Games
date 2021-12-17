package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class StandStillMiniGame extends CompleteMiniGame<StandStillMiniGame> {
    private boolean checkForMovement = false;
    private final BukkitTask enableMovementCheckingTask;

    public StandStillMiniGame(LlamaSaysGame game, Consumer<StandStillMiniGame> callback) {
        super(game, callback);
        enableMovementCheckingTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), () -> checkForMovement = true, 30);
    }

    @Override
    public Component getTitle() {
        return Component.text("Stand completely still");
    }

    @Override
    public void handleGameEnded() {
        for (Player player : game.getPlayers()) {
            addSuccessfulPlayer(player);
        }
        enableMovementCheckingTask.cancel();
    }

    @EventHandler
    public void handlePlayerMoveEvent(PlayerMoveEvent event) {
        if (checkForMovement && event.hasChangedPosition()) {
            addFailedPlayer(event.getPlayer());
        }
    }
}
