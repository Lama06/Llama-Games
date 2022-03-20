package io.github.lama06.llamaplugin.games.llama_says;

import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JumpIntoWaterMiniGame extends MiniGame {
    public JumpIntoWaterMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Jump into the water");
    }

    @Override
    public void handleGameStarted() {
        Set<BlockPosition> blocks = game.getConfig().getFloor().getBlocks();
        int amountOfWater = blocks.size() / 3;
        List<BlockPosition> waterBlocks = CollectionUtil.pickRandomElements(blocks, amountOfWater, game.getRandom());

        for (BlockPosition position : blocks) {
            Block block = game.getWorld().getBlockAt(position.asLocation(game.getWorld()));

            if (waterBlocks.contains(position)) {
                block.setBlockData(Material.WATER.createBlockData());
            } else {
                block.setBlockData(Material.BLUE_CONCRETE.createBlockData());
            }
        }

        fillPlatformBlocks(Material.ORANGE_STAINED_GLASS.createBlockData());

        for (Player player : game.getPlayers()) {
            player.teleport(getPlatformCenter().add(0, 1, 0).asLocation(game.getWorld()));
        }
    }

    private BlockPosition getPlatformCenter() {
        return game.getConfig().getFloorCenter().add(0, 20, 0);
    }

    private void fillPlatformBlocks(BlockData state) {
        BlockPosition platformCenter = getPlatformCenter();
        BlockArea platform = new BlockArea(platformCenter.add(1, 0, 1), platformCenter.add(-1, 0, -1));
        for (BlockPosition block : platform.getBlocks()) {
            block.getBlock(game.getWorld()).setBlockData(state);
        }
    }

    @EventHandler
    public void handlePlayerMoveEvent(PlayerMoveEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getPlayer().isInWater()) {
            result.addSuccessfulPlayer(event.getPlayer());
        }

        Block under = event.getTo().clone().add(0, -1, 0).getBlock();
        if (under.getType() == Material.BLUE_CONCRETE) {
            result.addFailedPlayer(event.getPlayer());
        }
    }

    @Override
    public void cleanupWorld() {
        fillPlatformBlocks(Material.AIR.createBlockData());
    }
}
