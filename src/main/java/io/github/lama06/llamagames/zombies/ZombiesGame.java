package io.github.lama06.llamagames.zombies;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.CollectionUtil;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.monster.*;
import io.github.lama06.llamagames.zombies.weapon.WeaponSystem;
import io.github.lama06.llamagames.zombies.weapon.WeaponSystemType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class ZombiesGame extends Game<ZombiesGame, ZombiesConfig> {
    private Set<ZombiesPlayer> zombiesPlayers;
    private int currentRound;
    private Map<MonsterType<?, ?>, Integer> remainingMonsters;
    private int spawnDelay;
    private BukkitTask spawnNextMonsterTask;
    private Set<Monster<?, ?>> monsters;
    private Set<String> unlockedAreas;
    private Set<MonsterSystem> monsterSystems;
    private Set<WeaponSystem> weaponSystems;

    public ZombiesGame(LlamaGamesPlugin plugin, World world, ZombiesConfig config, GameType<ZombiesGame, ZombiesConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted(String[] args) {
        int startRound = 1;
        if (args != null && args.length == 1) {
            try {
                startRound = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) { }
        }

        zombiesPlayers = new HashSet<>();
        for (Player player : world.getPlayers()) {
            zombiesPlayers.add(new ZombiesPlayer(this, player));
        }

        monsters = new HashSet<>();

        unlockedAreas = new HashSet<>();
        unlockedAreas.add(config.startArea);

        monsterSystems = new HashSet<>();
        for (MonsterSystemType<?> type : MonsterSystemType.getTypes()) {
            MonsterSystem system = type.creator().apply(this);
            monsterSystems.add(system);
            system.register();
        }

        weaponSystems = new HashSet<>();
        for (WeaponSystemType<?> type : WeaponSystemType.getTypes()) {
            WeaponSystem system = type.creator().apply(this);
            weaponSystems.add(system);
            system.register();
        }

        closeAllDoors();

        startRound(startRound);
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        for (ZombiesPlayer zombiesPlayer : zombiesPlayers) {
            zombiesPlayer.cleanup();
        }
        zombiesPlayers = null;

        for (Monster<?, ?> monster : monsters) {
            monster.remove();
        }
        monsters = null;

        unlockedAreas = null;

        if (spawnNextMonsterTask != null) {
            spawnNextMonsterTask.cancel();
            spawnNextMonsterTask = null;
        }

        for (MonsterSystem system : monsterSystems) {
            system.unregister();
        }
        monsterSystems = null;

        for (WeaponSystem system : weaponSystems) {
            system.unregister();
        }
        weaponSystems = null;

        closeAllDoors();
    }

    @Override
    public void handleGameLoaded() {
        closeAllDoors();
    }

    @Override
    public void handleGameUnloaded() {
        closeAllDoors();
    }

    @Override
    public boolean isConfigComplete() {
        return super.isConfigComplete() && config.isComplete();
    }

    @Override
    public boolean canStart() {
        return super.canStart() && world.getPlayers().size() >= 1;
    }

    @Override
    public void handlePlayerLeft(Player player) {
        ZombiesPlayer zombiesPlayer = getZombiesPlayer(player);
        if (zombiesPlayer != null) {
            zombiesPlayer.cleanup();
        }

        zombiesPlayers.removeIf(p -> p.getPlayer().equals(player));
    }

    @Override
    public boolean canContinueAfterPlayerLeft() {
        return getPlayers().size() >= 1;
    }

    private void startNextRound() {
        startRound(currentRound + 1);
    }

    private void startRound(int round) {
        currentRound = round;

        SpawnRate spawnRate = config.getSpawnRate(round);
        if (spawnRate == null) {
            endGame(GameEndReason.ENDED);
            return;
        }

        remainingMonsters = new HashMap<>(spawnRate.monsters);
        spawnDelay = spawnRate.delay;

        for (Player player : world.getPlayers()) {
            setSpectator(player, false);
        }

        getBroadcastAudience().showTitle(Title.title(
                Component.text("Round %d".formatted(round), NamedTextColor.YELLOW),
                Component.text("%d Zombies".formatted(remainingMonsters.values().stream().mapToInt(i -> i).sum()))
        ));

        spawnNextMonsterTask = Bukkit.getScheduler().runTaskLater(plugin, this::spawnNextMonster, spawnDelay);
    }

    private void spawnNextMonster() {
        cleanupRemainingMonsters();
        MonsterType<?, ?> monsterType = CollectionUtil.pickRandomElement(remainingMonsters.keySet());
        remainingMonsters.put(monsterType, remainingMonsters.get(monsterType) - 1);

        EntityPosition spawnLocation = getRandomMonsterSpawnPosition(monsterType.getSpawnLocation());

        spawnMonster(monsterType, spawnLocation);

        cleanupRemainingMonsters();
        if (!remainingMonsters.isEmpty()) {
            spawnNextMonsterTask = Bukkit.getScheduler().runTaskLater(plugin, this::spawnNextMonster, spawnDelay);
        } else {
            spawnNextMonsterTask = null;
        }
    }

    private EntityPosition getRandomMonsterSpawnPosition(MonsterSpawnLocation spawnLocation) {
        record SpawnData(EntityPosition position, String area) { }

        Set<SpawnData> possibleSpawns = (switch (spawnLocation) {
            case WINDOW -> config.windows.stream().map(window -> new SpawnData(window.spawnLocation, window.area));
            case ADDITIONAL_SPAWN_LOCATION -> config.additionalZombieSpawnLocations.stream().map(location -> new SpawnData(location.position, location.area));
        }).filter(spawnData -> unlockedAreas.contains(spawnData.area)).collect(Collectors.toSet());

        SpawnData spawnData = CollectionUtil.pickRandomElement(possibleSpawns, random);

        if (spawnData == null) return null;
        return spawnData.position;
    }

    private void cleanupRemainingMonsters() {
        remainingMonsters.values().removeIf(integer -> integer <= 0);
    }

    public Set<Monster<?, ?>> getMonsters() {
        return monsters;
    }

    public Monster<?, ?> getMonster(Entity entity) {
        for (Monster<?, ?> monster : monsters) {
            if (monster.getEntity().equals(entity)) {
                return monster;
            }
        }

        return null;
    }

    public void handleMonsterDied(Monster<?, ?> monster, ZombiesPlayer killedBy) {
        if (killedBy != null) {
            killedBy.giveGold(25);
        }

        monster.remove();
        monsters.remove(monster);

        if (monsters.isEmpty() && spawnNextMonsterTask == null) {
            startNextRound();
        }
    }

    public <T extends Monster<T, E>, E extends Entity> void spawnMonster(MonsterType<T, E> type, EntityPosition position) {
        T monster = type.getCreator().createMonster(this, type, world, position);
        monsters.add(monster);
    }

    private void closeAllDoors() {
        for (Door door : config.doors) {
            door.close(world);
        }
    }

    public void killPlayer(Player player) {
        setSpectator(player, true);

        if (getPlayers().isEmpty()) {
            endGame(GameEndReason.ENDED);
        }
    }

    @EventHandler
    public void listenForPlayerOpenDoor(PlayerInteractEvent event) {
        if (!running) {
            return;
        }

        if (!getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (!event.getAction().isLeftClick()) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        Optional<Door> door = config.doors.stream().filter(d -> d.activationBlock.equals(new BlockPosition(event.getClickedBlock().getLocation()))).findFirst();
        if (door.isEmpty()) {
            return;
        }

        ZombiesPlayer zombiesPlayer = getZombiesPlayer(event.getPlayer());
        if (zombiesPlayer == null) {
            return;
        }

        if (!zombiesPlayer.pay(door.get().gold)) {
            return;
        }

        door.get().open(world);
        unlockDoorArea(door.get());
    }

    private void unlockDoorArea(Door door) {
        if (unlockedAreas.contains(door.area1)) {
            unlockedAreas.add(door.area2);
        } else if (unlockedAreas.contains(door.area2)) {
            unlockedAreas.add(door.area1);
        }
    }

    @EventHandler
    public void listenForPlayerBuysWeapon(PlayerInteractEvent event) {
        if (!running) {
            return;
        }

        if (!getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (!event.getAction().isLeftClick()) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        Optional<WeaponShop> shop = config.weaponShops.stream()
                .filter(s -> s.activationBLock.equals(new BlockPosition(event.getClickedBlock().getLocation())))
                .findFirst();
        if (shop.isEmpty()) {
            return;
        }

        ZombiesPlayer zombiesPlayer = getZombiesPlayer(event.getPlayer());
        if (zombiesPlayer == null) {
            return;
        }

        zombiesPlayer.buyWeapon(shop.get());
    }

    @EventHandler
    public void regenerateHealth(ServerTickStartEvent event) {
        if (!running) {
            return;
        }

        if (event.getTickNumber() % 15 == 0) {
            for (ZombiesPlayer zombiesPlayer : zombiesPlayers) {
                zombiesPlayer.heal(1);
            }
        }
    }

    public Set<ZombiesPlayer> getZombiesPlayers() {
        return zombiesPlayers;
    }

    public ZombiesPlayer getZombiesPlayer(Player player) {
        return zombiesPlayers.stream().filter(zombiesPlayer -> zombiesPlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }
}
