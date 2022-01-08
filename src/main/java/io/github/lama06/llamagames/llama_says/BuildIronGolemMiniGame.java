package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BuildIronGolemMiniGame extends MiniGame {
    private Set<BlockPosition> blocks;

    public BuildIronGolemMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        blocks = new HashSet<>();
    }

    @Override
    public Component getTitle() {
        return Component.text("Build an iron golem");
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            PlayerInventory inventory = player.getInventory();
            inventory.setItem(0, new ItemStack(Material.IRON_BLOCK, 4));
            inventory.setItem(1, new ItemStack(Material.CARVED_PUMPKIN, 1));
        }

        game.getEventCanceler().setCancelPlayerBlockPlacement(false);
    }

    @Override
    public void cleanupWorld() {
        for (BlockPosition block : blocks) {
            game.getWorld().getBlockAt(block.asLocation(game.getWorld())).setBlockData(Material.AIR.createBlockData());
        }

        for (IronGolem ironGolem : game.getWorld().getEntitiesByClass(IronGolem.class)) {
            ironGolem.remove();
        }
    }

    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        blocks.add(new BlockPosition(event.getBlock().getLocation()));

        if (isBuildingIronGolem(event)) {
            result.addSuccessfulPlayer(event.getPlayer());
        } else if (event.getBlock().getType() == Material.CARVED_PUMPKIN) {
            result.addFailedPlayer(event.getPlayer());
        }
    }

    private static boolean isBuildingIronGolem(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.CARVED_PUMPKIN) {
            return false;
        }

        Block under1 = event.getBlock().getRelative(BlockFace.DOWN);
        Block under2 = under1.getRelative(BlockFace.DOWN);
        if (under1.getType() != Material.IRON_BLOCK || under2.getType() != Material.IRON_BLOCK) {
            return false;
        }

        Block north = under1.getRelative(BlockFace.NORTH);
        Block south = under1.getRelative(BlockFace.SOUTH);

        Block west = under1.getRelative(BlockFace.WEST);
        Block east = under1.getRelative(BlockFace.EAST);

        if ((north.getType() != Material.IRON_BLOCK || south.getType() != Material.IRON_BLOCK) &&
                (west.getType() != Material.IRON_BLOCK || east.getType() != Material.IRON_BLOCK)) {
            return false;
        }

        return true;
    }
}
