package io.github.lama06.lamagames.lama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.function.Consumer;

public abstract sealed class MiniGame<T extends MiniGame<T>> implements Listener permits CompeteMiniGame, CompleteMiniGame {
    protected final LamaSaysGame game;
    private final Consumer<T> callback;
    private BukkitTask timeoutTask;

    public MiniGame(LamaSaysGame game, Consumer<T> callback) {
        this.game = game;
        this.callback = callback;
    }

    public abstract Component getTitle();

    public int getTimeoutDelay() {
        return 200;
    }

    public abstract void handleGameStarted();

    public void handleGameEnded() { }

    public final void startGame() {
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());

        for (Player player : game.getWorld().getPlayers()) {
            player.teleport(game.getConfig().spawnPoint.asLocation(game.getWorld()));
        }

        Component title = getTitle();

        game.getBroadcastAudience().sendMessage(title.color(NamedTextColor.YELLOW));

        for (Player player : game.getPlayers()) {
            player.showTitle(Title.title(
                    title.color(NamedTextColor.YELLOW),
                    Component.empty(),
                    Title.Times.of(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            ));
        }

        timeoutTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), () -> endGame(true), getTimeoutDelay());

        handleGameStarted();
    }

    @SuppressWarnings("unchecked")
    public final void endGame(boolean callCallback) {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }

        HandlerList.unregisterAll(this);

        handleGameEnded();

        for (Player player : game.getPlayers()) {
            player.getInventory().clear();

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activePotionEffect.getType());
            }
        }

        if (callCallback) {
            callback.accept((T) this);
        }
    }
}
