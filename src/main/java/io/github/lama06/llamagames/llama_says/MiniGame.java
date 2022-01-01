package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.function.Consumer;

public abstract class MiniGame implements Listener {
    protected final LlamaSaysGame game;
    protected final MiniGameResult result;
    private final Consumer<MiniGame> callback;
    private BukkitTask timeoutTask;

    public MiniGame(LlamaSaysGame game, MiniGameResult result, Consumer<MiniGame> callback) {
        this.game = game;
        this.result = result;
        this.callback = callback;
    }

    public abstract Component getTitle();

    public int getTimeoutDelay() {
        return 200;
    }

    public void init() { }

    public void handleGameStarted() { }

    public void handleGameEnded() { }

    public void cleanupPlayer(Player player) { }

    public void cleanupWorld() { }

    public void cleanup() { }

    private void cleanupPlayerInternal(Player player) {
        System.out.println(player.getName());
        player.getInventory().clear();

        player.setFoodLevel(20);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setLevel(0);
        player.setExp(0);

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }

        cleanupPlayer(player);
    }

    private void cleanupWorldInternal() {
        cleanupWorld();
    }

    public final void handlePlayerLeft(Player player) {
        cleanupPlayerInternal(player);
    }

    public final void startGame() {
        init();

        for (Player player : game.getWorld().getPlayers()) {
            player.teleport(game.getConfig().getSpawnPoint().asLocation(game.getWorld()));
        }

        Component title = getTitle();

        game.getBroadcastAudience().sendMessage(title.color(NamedTextColor.YELLOW));
        game.getBroadcastAudience().showTitle(Title.title(
                title.color(NamedTextColor.YELLOW),
                Component.empty(),
                Title.Times.of(
                        Duration.ZERO,
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(1)
                )
        ));

        timeoutTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), () -> endGame(true), getTimeoutDelay());

        handleGameStarted();

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public final void endGame(boolean callCallback) {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }

        HandlerList.unregisterAll(this);

        for (Player player : game.getPlayers()) {
            cleanupPlayerInternal(player);
        }

        cleanupWorldInternal();

        cleanup();

        handleGameEnded();

        if (callCallback) {
            callback.accept(this);
        }
    }

    public MiniGameResult getResult() {
        return result;
    }
}
