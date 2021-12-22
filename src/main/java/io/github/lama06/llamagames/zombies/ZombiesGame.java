package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.Util;
import io.github.lama06.llamagames.zombies.weapon.WeaponShop;
import io.github.lama06.llamagames.zombies.zombie.AbstractZombie;
import io.github.lama06.llamagames.zombies.zombie.ZombieType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class ZombiesGame extends Game<ZombiesGame, ZombiesConfig> {
    private Map<UUID, ZombiesPlayer> zombiePlayers;
    private Set<String> unlockedAreas;
    private boolean powerSwitchActivated;
    private int currentRound;
    private Map<ZombieType<?, ?>, Integer> remainingZombiesToSpawn;
    private Set<AbstractZombie<?, ?>> zombies;
    private BukkitTask spawnZombiesTask;

    public ZombiesGame(LlamaGamesPlugin plugin, World world, ZombiesConfig config, GameType<ZombiesGame, ZombiesConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted() {
        closeAllDoors();

        unlockedAreas = new HashSet<>();
        unlockedAreas.add(config.startArea);

        powerSwitchActivated = false;

        zombiePlayers = new HashMap<>();

        for (Player player : getPlayers()) {
            zombiePlayers.put(player.getUniqueId(), new ZombiesPlayer(this, player));
        }

        startRound(1);
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        closeAllDoors();

        unlockedAreas = null;
        zombiePlayers = null;

        if (spawnZombiesTask != null) {
            spawnZombiesTask.cancel();
            spawnZombiesTask = null;
        }

        for (AbstractZombie<?, ?> zombie : zombies) {
            zombie.remove(AbstractZombie.ZombieRemoveReason.GAME_UNLOAD);
        }
    }

    @Override
    public void handlePlayerLeft(Player player) {
        zombiePlayers.remove(player.getUniqueId());
    }

    @Override
    public boolean canStart() {
        return world.getPlayers().size() >= 1;
    }

    private void startRound(int round) {
        this.currentRound = round;
        this.remainingZombiesToSpawn = new HashMap<>(getCurrentSpawnRate().zombies);

        spawnNextZombie();
    }

    private void startNextRound() {
        startRound(currentRound+1);
    }

    private Set<ZombieType<?, ?>> getRemainingZombieTypes() {
        return remainingZombiesToSpawn.keySet().stream().filter(type -> remainingZombiesToSpawn.get(type) != 0).collect(Collectors.toSet());
    }

    private <T extends AbstractZombie<T, E>, E extends Entity> void spawnZombie(ZombieType<T, E> type, BlockPosition position) {
        T zombie = type.getCreator().createZombie(this, type);
        zombie.spawn(position);
        zombies.add(zombie);
    }

    private void spawnNextZombie() {
        Set<ZombieType<?, ?>> remainingZombieTypes = getRemainingZombieTypes();

        if (remainingZombieTypes.isEmpty()) {
            spawnZombiesTask = null;
            return;
        }

        ZombieType<?, ?> selectedZombieType = Util.pickRandomElement(remainingZombieTypes, random);
        BlockPosition spawnLocation = getZombieSpawnLocation(selectedZombieType);

        spawnZombie(selectedZombieType, spawnLocation);

        if (getRemainingZombieTypes().isEmpty()) {
            spawnZombiesTask = null;
            return;
        }

        spawnZombiesTask = Bukkit.getScheduler().runTaskLater(plugin, this::spawnNextZombie, getCurrentSpawnRate().delay);
    }

    private BlockPosition getZombieSpawnLocation(ZombieType<?, ?> type) {
        Player randomPlayer = Util.pickRandomElement(getPlayers());
        BlockPosition position = new BlockPosition(randomPlayer.getLocation());

        return switch (type.getSpawnType()) {
            case WINDOW -> config.windows.stream()
                    .filter(window -> unlockedAreas.contains(window.area))
                    .map(window -> window.zombieSpawnLocation)
                    .min((p1, p2) -> {
                        BlockPosition.Distance distance1 = p1.getDistanceTo(position);
                        BlockPosition.Distance distance2 = p2.getDistanceTo(position);
                        return distance1.compareTo(distance2);
                    }).orElse(config.spawnPoint);
            case ADDITIONAL_SPAWN_LOCATION -> config.additionalSpawnLocations.stream()
                    .filter(location -> unlockedAreas.contains(location.area))
                    .map(location -> location.position)
                    .min((p1, p2) -> {
                        BlockPosition.Distance distance1 = p1.getDistanceTo(position);
                        BlockPosition.Distance distance2 = p2.getDistanceTo(position);
                        return distance1.compareTo(distance2);
                    }).orElse(config.spawnPoint);
        };
    }

    private SpawnRate getCurrentSpawnRate() {
        if (config.spawnRates.containsKey(currentRound)) {
            return config.spawnRates.get(currentRound);
        }

        if (config.spawnRates.containsKey(-1)) {
            return config.spawnRates.get(-1);
        }

        throw new IllegalStateException("No spawn rate configuration for round %d".formatted(currentRound));
    }

    public void removeZombie(AbstractZombie<?, ?> zombie, AbstractZombie.ZombieRemoveReason reason) {
        zombies.remove(zombie);
        zombie.onRemove(reason);

        if (zombies.size() == 0 && spawnZombiesTask == null && reason != AbstractZombie.ZombieRemoveReason.GAME_UNLOAD) {
            startNextRound();
        }
    }

    public AbstractZombie<?, ?> getZombie(Entity entity) {
        return zombies.stream().filter(zombie -> zombie.getEntity().equals(entity)).findFirst().orElse(null);
    }

    private void closeAllDoors() {
        for (Door door : config.doors) {
            door.close(world);
        }
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (!getPlayers().contains(event.getPlayer())) {
            return;
        }

        ZombiesPlayer player = zombiePlayers.get(event.getPlayer().getUniqueId());

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        BlockPosition clickedPosition = new BlockPosition(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());

        if (config.powerSwitch.position.equals(clickedPosition)) {
            powerSwitchActivated = true;
            return;
        }

        for (WeaponShop weaponShop : config.weaponShops) {
            if (weaponShop.activationBlock.equals(clickedPosition)) {
                player.buyWeapon(weaponShop.weapon, event.getPlayer().getInventory().getHeldItemSlot(), weaponShop.gold);
                return;
            }
        }

        for (Door door : config.doors) {
            if (door.activationBlock.equals(clickedPosition)) {
                return;
            }
        }
    }
}
