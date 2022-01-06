package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.CollectionUtil;
import io.github.lama06.llamagames.util.MinecraftColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void handleGameStarted(String[] args) {
        remainingFloors = new HashSet<>(config.getFloors());

        startRound(1);
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
        return super.canStart() && world.getPlayers().size() != 0;
    }

    @Override
    public boolean isConfigComplete() {
        return super.isConfigComplete() && config.getDeadlyBlock() != null && config.getFloor() != null && !config.getFloors().isEmpty() && config.getRoundTimes().containsKey(-1);
    }

    @Override
    public boolean canContinueAfterPlayerLeft() {
        return getPlayers().size() >= 1;
    }

    @EventHandler
    private void killPlayersThatTouchDeadlyBlocks(PlayerMoveEvent event) {
        if (!getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getTo().clone().add(0, -1, 0).getBlock().getType() != config.getDeadlyBlock()) {
            return;
        }

        setSpectator(event.getPlayer(), true);

        if (getPlayers().size() == 0) {
            endGame(GameEndReason.ENDED);
        }
    }

    private Floor getNextFloor() {
        if (remainingFloors.isEmpty()) {
            return CollectionUtil.pickRandomElement(config.getFloors(), random);
        }

        Floor floor = CollectionUtil.pickRandomElement(remainingFloors);
        remainingFloors.remove(floor);
        return floor;
    }

    private Set<Material> getBlockTypes(Floor floor) {
        Set<Material> result = new HashSet<>();

        for (BlockPosition position : floor.getArea().getBlocks()) {
            Block block = world.getBlockAt(position.asLocation(world));
            if (!block.getType().isAir()) {
                result.add(block.getType());
            }
        }

        return result;
    }

    private int getRoundTime(int round) {
        if (config.getRoundTimes().containsKey(round)) {
            return config.getRoundTimes().get(round);
        }

        if (config.getRoundTimes().containsKey(-1)) {
            return config.getRoundTimes().get(-1);
        }

        return 40;
    }

    private void removeFloorBlocks(Material material) {
        for (BlockPosition position : config.getFloor().getBlocks()) {
            Block block = world.getBlockAt(position.asLocation(world));

            if (block.getType() != material) {
                block.setType(Material.AIR);
            }
        }
    }

    private void startRound(int round) {
        this.currentRound = round;

        int roundTime = getRoundTime(round);

        Floor floor = getNextFloor();
        setFloor(floor);

        Material type = CollectionUtil.pickRandomElement(getBlockTypes(floor));
        MinecraftColor color = MinecraftColor.getColorOfMaterial(type);
        if (color == null) {
            color = MinecraftColor.WHITE;
        }

        getBroadcastAudience().showTitle(Title.title(
                Component.translatable(type).color(color.getTextColor()),
                Component.text("Round %d: Stand on ".formatted(round)).append(Component.translatable(type)).color(color.getTextColor()),
                Title.Times.of(
                        Duration.ZERO,
                        Duration.ofSeconds(roundTime/20),
                        Duration.ofMillis(500)
                )
        ));

        currentTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeFloorBlocks(type);

            Bukkit.getScheduler().runTaskLater(plugin, this::startNextRound, 40);
        }, roundTime);
    }

    private void startNextRound() {
        startRound(currentRound + 1);
    }

    private void setFloor(Floor floor) {
        floor.getArea().clone(world, config.getFloor());
    }

    private void clearFloor() {
        config.getFloor().fill(world, Material.AIR.createBlockData());
    }
}
