package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DoNotStandOnIceMiniGame extends MiniGame {
    private BukkitTask addMoreIceTask;

    public DoNotStandOnIceMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(game), callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Don't stand on ice");
    }

    @Override
    public void handleGameStarted() {
        Set<BlockPosition> blocks = game.getConfig().getFloor().getBlocks();
        int amountOfIceBlocks = blocks.size() / 2;
        List<BlockPosition> iceBlocks = CollectionUtil.pickRandomElements(blocks, amountOfIceBlocks, game.getRandom());

        for (BlockPosition iceBlock : iceBlocks) {
            game.getWorld().setBlockData(iceBlock.asLocation(game.getWorld()), Material.BLUE_ICE.createBlockData());
        }

        for (BlockPosition position : blocks) {
            Block block = game.getWorld().getBlockAt(position.asLocation(game.getWorld()));
            if (block.getType() != Material.BLUE_ICE) {
                block.setBlockData(Material.SNOW_BLOCK.createBlockData());
            }
        }

        addMoreIceTask = Bukkit.getScheduler().runTaskLater(game.getPlugin(), this::addMoreIce, 5*20);
    }

    private void addMoreIce() {
        Set<BlockPosition> snowBlocks = game.getConfig().getFloor().getBlocks().stream()
                .filter(pos -> game.getWorld().getBlockAt(pos.asLocation(game.getWorld())).getType() != Material.BLUE_ICE)
                .collect(Collectors.toSet());

        int amount = snowBlocks.size() / 3;
        for (BlockPosition position : CollectionUtil.pickRandomElements(snowBlocks, amount, game.getRandom())) {
            game.getWorld().setBlockData(position.asLocation(game.getWorld()), Material.BLUE_ICE.createBlockData());
        }
    }

    @Override
    public void cleanup() {
        addMoreIceTask.cancel();
    }

    @Override
    public void handleGameEnded() {
        for (Player player : game.getPlayers()) {
            Block under = game.getWorld().getBlockAt(player.getLocation().add(0, -1, 0));

            if (under.getType() == Material.SNOW_BLOCK) {
                result.addSuccessfulPlayer(player);
            } else {
                result.addFailedPlayer(player);
            }
        }
    }
}
