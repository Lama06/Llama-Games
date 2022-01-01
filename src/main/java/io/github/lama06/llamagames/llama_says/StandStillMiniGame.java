package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class StandStillMiniGame extends MiniGame {
    private boolean checkForMovement = false;
    private BukkitTask enableMovementCheckingTask;

    public StandStillMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(), callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Stand completely still");
    }

    @Override
    public void handleGameStarted() {
        enableMovementCheckingTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), () -> checkForMovement = true, 40);
    }

    @Override
    public void handleGameEnded() {
        for (Player player : game.getPlayers()) {
            result.addSuccessfulPlayer(player);
        }
    }

    @Override
    public void cleanup() {
        if (enableMovementCheckingTask != null) {
            enableMovementCheckingTask.cancel();
        }
    }

    @EventHandler
    public void handlePlayerMoveEvent(PlayerMoveEvent event) {
        if (checkForMovement && event.hasChangedPosition()) {
            result.addFailedPlayer(event.getPlayer());
        }
    }
}
