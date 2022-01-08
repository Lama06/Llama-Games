package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class SmeltMiniGame extends MiniGame {
    private static final List<Pair<Material, Material>> INGREDIENTS = List.of(
            Pair.of(Material.RAW_COPPER, Material.COPPER_INGOT),
            Pair.of(Material.RAW_IRON, Material.IRON_INGOT),
            Pair.of(Material.RAW_GOLD, Material.GOLD_INGOT)
    );

    private static final List<Material> FURNACE_TYPES = List.of(
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER
    );

    private Pair<Material, Material> ingredient;

    public SmeltMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        ingredient = INGREDIENTS.get(game.getRandom().nextInt(INGREDIENTS.size()));
    }

    @Override
    public Component getTitle() {
        return Component.text("Smelt ").append(Component.translatable(ingredient.getLeft()));
    }

    @Override
    public void handleGameStarted() {
        for (int i = 0; i < FURNACE_TYPES.size(); i++) {
            Material furnaceType = FURNACE_TYPES.get(i);
            game.getWorld().setBlockData(game.getConfig().getFloorCenter().asLocation().add(0, 1 + i, 0), furnaceType.createBlockData());
        }

        for (Player player : game.getPlayers()) {
            player.getInventory().setItem(0, new ItemStack(Material.LAVA_BUCKET));

            for (int slot = 1, ingredient = 0; slot <= 8 && ingredient < INGREDIENTS.size(); slot++, ingredient++) {
                player.getInventory().setItem(slot, new ItemStack(INGREDIENTS.get(ingredient).getLeft()));
            }
        }

        game.getEventCanceler().setCancelInventoryEvents(false);
    }

    @Override
    public void cleanupWorld() {
        for (int i = 0; i < FURNACE_TYPES.size(); i++) {
            game.getWorld().setBlockData(game.getConfig().getFloorCenter().asLocation().add(0, 1 + i, 0), Material.AIR.createBlockData());
        }
    }

    @EventHandler
    public void handleFurnaceStartSmeltEvent(FurnaceStartSmeltEvent event) {
        if (!event.getBlock().getWorld().equals(game.getWorld())) {
            return;
        }

        if (event.getSource().getType() != ingredient.getLeft()) {
            return;
        }

        event.setTotalCookTime(1);
    }

    @EventHandler
    public void handleFurnaceExtractEvent(FurnaceExtractEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getItemType() == ingredient.getRight()) {
            result.addSuccessfulPlayer(event.getPlayer());
        } else {
            result.addFailedPlayer(event.getPlayer());
        }
    }
}
