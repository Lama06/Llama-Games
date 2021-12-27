package io.github.lama06.llamagames.blockparty;

import com.google.common.collect.ImmutableMap;
import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
import java.util.Map;
import java.util.Set;

public class BlockPartyGame extends Game<BlockPartyGame, BlockPartyConfig> {
    private static final Map<Material, TextColor> MATERIAL_TO_COLOR = ImmutableMap.<Material, TextColor>builder()
            .put(Material.WHITE_WOOL, NamedTextColor.WHITE)
            .put(Material.BLACK_WOOL, NamedTextColor.BLACK)
            .put(Material.RED_WOOL, NamedTextColor.RED)
            .put(Material.BLUE_WOOL, NamedTextColor.BLUE)
            .put(Material.BROWN_WOOL, TextColor.color(130, 84, 50))
            .put(Material.CYAN_WOOL, TextColor.color(22, 156, 157))
            .put(Material.GRAY_WOOL, NamedTextColor.GRAY)
            .put(Material.GREEN_WOOL, NamedTextColor.GREEN)
            .put(Material.LIGHT_BLUE_WOOL, TextColor.color(58, 179, 218))
            .put(Material.LIGHT_GRAY_WOOL, TextColor.color(156, 157, 151))
            .put(Material.LIME_WOOL, TextColor.color(128, 199, 31))
            .put(Material.MAGENTA_WOOL, TextColor.color(198, 79, 189))
            .put(Material.ORANGE_WOOL, TextColor.color(249, 128, 29))
            .put(Material.PINK_WOOL, TextColor.color(243, 140, 170))
            .put(Material.PURPLE_WOOL, TextColor.color(137, 50, 183))
            .put(Material.YELLOW_WOOL, NamedTextColor.YELLOW)
            .build();

    private int currentRound;
    private Set<Floor> remainingFloors = new HashSet<>();
    private BukkitTask currentTask;

    public BlockPartyGame(LlamaGamesPlugin plugin, World world, BlockPartyConfig config, GameType<BlockPartyGame, BlockPartyConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted() {
        remainingFloors = new HashSet<>(config.floors);

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
        return super.isConfigComplete() && config.deadlyBlock != null && config.floor != null && !config.floors.isEmpty() && config.roundTimes.containsKey(-1);
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

        if (event.getTo().clone().add(0, -1, 0).getBlock().getType() != config.deadlyBlock) {
            return;
        }

        setSpectator(event.getPlayer(), true);

        if (getPlayers().size() == 0) {
            endGame(GameEndReason.ENDED);
        }
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

    private void removeFloorBlocks(Material material) {
        for (BlockPosition position : config.floor.getBlocks()) {
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

        Material type = Util.pickRandomElement(getBlockTypes(floor));
        TextColor color = MATERIAL_TO_COLOR.getOrDefault(type, NamedTextColor.WHITE);

        getBroadcastAudience().showTitle(Title.title(
                Component.translatable(type).color(color),
                Component.text("Round %d: Stand on ".formatted(round)).append(Component.translatable(type)).color(color),
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
        floor.area.clone(world, config.floor);
    }

    private void clearFloor() {
        config.floor.fill(world, Material.AIR.createBlockData());
    }
}
