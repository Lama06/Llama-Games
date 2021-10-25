package io.github.lama06.lamagames;

import com.google.gson.TypeAdapter;
import io.github.lama06.lamagames.util.Pair;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public abstract class Game<G extends Game<G, C>, C> implements Listener {
    protected LamaGamesPlugin plugin;
    protected GameType<G, C> type;
    protected World world;
    protected C config;
    protected boolean running = false;
    protected final Set<UUID> players = new HashSet<>();
    private BukkitTask countdownTask = null;

    public Game(LamaGamesPlugin plugin, World world, C config, GameType<G, C> type) {
        this.plugin = plugin;
        this.type = type;
        this.world = world;
        this.config = config;
    }

    public final void startGame() {
        if (running || !canStart()) return;

        running = true;
        handleGameStarted();
    }

    public final void endGame() {
        if (!running) return;

        running = false;
        handleGameEnded();

        players.clear();
        addAllPlayers();
    }

    public void handleGameLoaded(C config) {
        this.config = config;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        addAllPlayers();

        if (canStart()) {
            startAfterCountdown();
        }
    }

    public void handleGameUnloaded() {
        HandlerList.unregisterAll(this);

        if (countdownTask != null) {
            countdownTask.cancel();
        }
    }

    public abstract Set<Pair<Class<?>, TypeAdapter<?>>> getConfigTypeAdapters();

    public abstract void handleGameStarted();

    public abstract void handleGameEnded();

    public abstract boolean canStart();

    public void handlePlayerLeft(Player player) { }

    @EventHandler
    public void handlePlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        if (event.getFrom().equals(world)) {
            players.remove(event.getPlayer().getUniqueId());

            handlePlayerLeft(event.getPlayer());

            if (world.getPlayers().size() == 0) {
                endGame();
            }
        } else if (event.getPlayer().getWorld().equals(world)) {
            if (running) {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getPlayer().sendTitle("You are not spectating", null, 20, 60, 20);
            } else {
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
                players.add(event.getPlayer().getUniqueId());

                startAfterCountdown();
            }
        }
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        if (players.stream().anyMatch(id -> event.getPlayer().getUniqueId().equals(id))) {
            players.remove(event.getPlayer().getUniqueId());
            handlePlayerLeft(event.getPlayer());
        }
    }

    private void addAllPlayers() {
        players.addAll(world.getPlayers().stream().map(Player::getUniqueId).toList());
    }

    private void startAfterCountdown() {
        startAfterCountdown(10);
    }

    public void startAfterCountdown(int countdown) {
        if (countdown == 0) {
            countdownTask = null;
            if (canStart()) {
                startGame();
            } else {
                broadcast(new ComponentBuilder().color(ChatColor.RED).append("Start Failed").create());
            }
        } else {
            for (Player player : world.getPlayers()) {
                player.sendTitle(ChatColor.GREEN.toString() + countdown, null, 0, 20, 0);
            }
            countdownTask = Bukkit.getScheduler().runTaskLater(plugin, () -> startAfterCountdown(countdown - 1), 20);
        }
    }

    public void broadcast(BaseComponent... msg) {
        for (Player player : world.getPlayers()) {
            player.spigot().sendMessage(msg);
        }
    }

    public LamaGamesPlugin getPlugin() {
        return plugin;
    }

    public GameType<G, C> getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }

    public C getConfig() {
        return config;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Set<Player> getPlayers() {
        Set<Player> result = new HashSet<>(players.size());

        Iterator<UUID> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = Bukkit.getPlayer(iterator.next());
            if (player == null) {
                iterator.remove();
                continue;
            }
            result.add(player);
        }

        return result;
    }
}
