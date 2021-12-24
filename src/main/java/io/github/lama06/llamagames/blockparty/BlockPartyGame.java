package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class BlockPartyGame extends Game<BlockPartyGame, BlockPartyConfig> {
    private int currentRound;
    private Set<Floor> remainingFloors = new HashSet<>();
    private BukkitTask currentTask;

    public BlockPartyGame(LlamaGamesPlugin plugin, World world, BlockPartyConfig config, GameType<BlockPartyGame, BlockPartyConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted() {
        remainingFloors = new HashSet<>(config.floors);
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        remainingFloors = null;

        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
        }

        clearFloor();
    }

    @Override
    public boolean canStart() {
        return false;
    }

    private Floor getNextFloor() {
        if (remainingFloors.isEmpty()) {
            return Util.pickRandomElement(config.floors, random);
        }

        Floor floor = Util.pickRandomElement(remainingFloors);
        remainingFloors.remove(floor);
        return floor;
    }

    private Set<Material> getBlockTypes(Floor floor) {
        Set<Material> result = new HashSet<>();

        for (BlockPosition position : floor.area.getBlocks()) {
            Block block = world.getBlockAt(position.asLocation(world));
            if (!block.getType().isAir()) {
                result.add(block.getType());
            }
        }

        return result;
    }

    private int getRoundTime(int round) {
        if (config.roundTimes.containsKey(round)) {
            return config.roundTimes.get(round);
        }

        if (config.roundTimes.containsKey(-1)) {
            return config.roundTimes.get(-1);
        }

        return 40;
    }

    private void removeFloorBlock(Material material) {
        for (BlockPosition position : config.floor.getBlocks()) {
            Block block = world.getBlockAt(position.asLocation(world));

            if (block.getType() == material) {
                block.setType(Material.AIR);
            }
        }
    }

    private void startRound(int round) {
        this.currentRound = round;

        int roundTime = getRoundTime(round);

        Floor floor = getNextFloor();
        setFloor(floor);

        Material type = Util.pickRandomElement(getBlockTypes(floor));

        getBroadcastAudience().showTitle(Title.title(
                Component.translatable(type),
                Component.text("Round %d: Stand on ".formatted(round)).append(Component.translatable(type)),
                Title.Times.of(
                        Duration.ofMillis(0),
                        Duration.ofSeconds(roundTime/20),
                        Duration.ofMillis(500)
                )
        ));

        currentTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeFloorBlock(type);

            Bukkit.getScheduler().runTaskLater(plugin, this::startNextRound, 40);
        }, roundTime);
    }

    private void startNextRound() {
        startRound(currentRound + 1);
    }

    private void setFloor(Floor floor) {
        floor.area.clone(world, config.floor);
    }

    private void clearFloor() {
        config.floor.fill(world, Material.AIR.createBlockData());
    }
}
