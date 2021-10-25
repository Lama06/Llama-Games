package io.github.lama06.lamagames.lama_says;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public abstract class MiniGame<T extends MiniGame<T>> implements Listener {
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

        game.broadcast(new ComponentBuilder().color(ChatColor.YELLOW).append(title).create());

        for (Player player : game.getPlayers()) {
            player.sendTitle(ChatColor.YELLOW + title, null, 10, 40, 10);
        }

        timeoutTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), this::endGame, 10);

        handleGameStarted();
    }

    @SuppressWarnings("unchecked")
    public final void endGame() {
        if (!timeoutTask.isCancelled()) {
            timeoutTask.cancel();
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
