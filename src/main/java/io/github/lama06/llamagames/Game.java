package io.github.lama06.llamagames;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Game<G extends Game<G, C>, C extends GameConfig> implements Listener {
    protected final LlamaGamesPlugin plugin;
    protected final GameType<G, C> type;
    protected final World world;
    protected final C config;
    protected boolean running = false;
    protected final EventCanceler canceler;
    protected final Random random = new Random();
    private Set<UUID> players = new HashSet<>();
    private BukkitTask countdownTask = null;

    public Game(LlamaGamesPlugin plugin, World world, C config, GameType<G, C> type) {
        this.plugin = plugin;
        this.world = world;
        this.config = config;
        this.type = type;

        canceler = new EventCanceler(plugin, this);
        canceler.disallowAll();
    }

    public final boolean startGame(String[] args) {
        if (!canStart()) {
            return false;
        }

        players = new HashSet<>();
        for (Player player : world.getPlayers()) {
            players.add(player.getUniqueId());
            player.teleport(config.getSpawnPoint().asLocation(world));
            player.setGameMode(GameMode.SURVIVAL);
        }

        running = true;

        handleGameStarted(args);

        return true;
    }

    public final boolean startGame() {
        return startGame(null);
    }

    public final boolean endGame(GameEndReason reason) {
        if (!running) {
            if (countdownTask != null) {
                countdownTask.cancel();
                countdownTask = null;
                return true;
            }

            return false;
        }

        running = false;
        handleGameEnded(reason);

        players = null;

        if (reason.isShouldAttemptToStartNextGame()) {
            tryToStartAfterCountdown();
        }

        return true;
    }

    public final void loadGame() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        canceler.registerEvents();

        if (!isConfigComplete()) {
            plugin.getLogger().warning("The configuration for the following game is not complete: %s".formatted(world.getName()));
        }

        handleGameLoaded();

        tryToStartAfterCountdown();
    }

    public final void unloadGame() {
        endGame(GameEndReason.UNLOAD);

        HandlerList.unregisterAll(this);
        canceler.unregisterEvents();

        handleGameUnloaded();
    }

    public void handleGameLoaded() { }

    public void handleGameUnloaded() { }

    public abstract void handleGameStarted(String[] args);

    public abstract void handleGameEnded(GameEndReason reason);

    public boolean isConfigComplete() {
        return config.getSpawnPoint() != null;
    }

    public boolean canStart() {
        return !running && isConfigComplete() && !isStarting();
    }

    public abstract boolean canContinueAfterPlayerLeft();

    public void handlePlayerLeft(Player player) { }

    protected void setSpectator(Player player, boolean spectator) {
        if (spectator) {
            players.remove(player.getUniqueId());

            player.setGameMode(GameMode.SPECTATOR);
            player.showTitle(Title.title(
                    Component.text("You are now spectating"),
                    Component.empty(),
                    Title.Times.of(Duration.ofSeconds(2), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
        } else {
            players.add(player.getUniqueId());

            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    protected boolean isSpectator(Player player) {
        return !getPlayers().contains(player);
    }

    private void handlePlayerJoinedInternal(Player player) {
        if (running) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("You are now in spectator mode as the game you joined is already running"));
        } else {
            tryToStartAfterCountdown();
        }

        player.teleport(config.getSpawnPoint() == null ? world.getSpawnLocation() : config.getSpawnPoint().asLocation(world));
    }

    private void handlePlayerLeftInternal(Player player) {
        if (running && !isSpectator(player)) {
            players.remove(player.getUniqueId());
            handlePlayerLeft(player);

            if (!canContinueAfterPlayerLeft()) {
                endGame(GameEndReason.PLAYER_LEFT);
            }
        }
    }

    @EventHandler
    public void handlePlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        if (event.getFrom().equals(world)) {
            handlePlayerLeftInternal(event.getPlayer());
        } else if (event.getPlayer().getWorld().equals(world)) {
            handlePlayerJoinedInternal(event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().equals(world)) {
            handlePlayerJoinedInternal(event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        if (getPlayers().contains(event.getPlayer())) {
            handlePlayerLeftInternal(event.getPlayer());
        }
    }

    private void tryToStartAfterCountdown() {
        if (!canStart()) {
            return;
        }

        startAfterCountdown(10);
    }

    private void startAfterCountdown(int countdown) {
        if (countdown == 0) {
            countdownTask = null;

            if (!startGame()) {
                getBroadcastAudience().sendMessage(Component.text("Start failed").color(NamedTextColor.RED));
            }
        } else {
            for (Player player : world.getPlayers()) {
                player.showTitle(Title.title(
                        Component.text(countdown).color(NamedTextColor.GREEN),
                        Component.empty(),
                        Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
                ));
            }

            countdownTask = Bukkit.getScheduler().runTaskLater(plugin, () -> startAfterCountdown(countdown - 1), 20);
        }
    }

    public Audience getBroadcastAudience() {
        return Audience.audience(world.getPlayers());
    }

    public LlamaGamesPlugin getPlugin() {
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

    public boolean isStarting() {
        return countdownTask != null && !countdownTask.isCancelled();
    }

    public Random getRandom() {
        return random;
    }

    public EventCanceler getEventCanceler() {
        return canceler;
    }

    public Set<Player> getPlayers() {
        if (players == null) {
            return Collections.emptySet();
        }

        Set<Player> result = new HashSet<>(players.size());

        Iterator<UUID> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = Bukkit.getPlayer(iterator.next());
            if (player == null || !player.getWorld().equals(world)) {
                iterator.remove();
                continue;
            }
            result.add(player);
        }

        return result;
    }

    public Set<UUID> getPlayerUUUIDs() {
        return getPlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    public enum GameEndReason {
        UNLOAD(false),
        COMMAND(false),
        PLAYER_LEFT(false),
        ENDED(true);

        private final boolean shouldAttemptToStartNextGame;

        GameEndReason(boolean shouldAttemptToStartNextGame) {
            this.shouldAttemptToStartNextGame = shouldAttemptToStartNextGame;
        }

        public boolean isShouldAttemptToStartNextGame() {
            return shouldAttemptToStartNextGame;
        }
    }
}
