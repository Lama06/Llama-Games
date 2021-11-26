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

    public abstract String getTitle();

    public abstract void handleGameStarted();

    public void handleGameEnded() { }

    public final void startGame() {
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());

        String title = getTitle();

        game.getBroadcastAudience().sendMessage(Component.text(title).color(NamedTextColor.YELLOW));

        for (Player player : game.getPlayers()) {
            player.showTitle(Title.title(
                    Component.text(title).color(NamedTextColor.YELLOW),
                    Component.empty(),
                    Title.Times.of(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            ));
        }

        timeoutTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), this::endGame, 10);

        handleGameStarted();
    }

    @SuppressWarnings("unchecked")
    public final void endGame() {
        if (timeoutTask != null && !timeoutTask.isCancelled()) {
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

        callback.accept((T) this);
    }
}
