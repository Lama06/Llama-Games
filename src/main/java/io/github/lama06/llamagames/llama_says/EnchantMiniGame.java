package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EnchantMiniGame extends MiniGame {
    private static final Set<Material> SWORDS = Set.of(
            Material.WOODEN_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );

    private Material sword;
    private List<ItemStack> itemsOrder;

    public EnchantMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        sword = CollectionUtil.pickRandomElement(SWORDS, game.getRandom());

        Set<ItemStack> items = SWORDS.stream().map(ItemStack::new).collect(Collectors.toSet());
        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
        items.add(new ItemStack(Material.LAPIS_LAZULI, 64));

        itemsOrder = CollectionUtil.pickRandomElements(items, 9, game.getRandom());
    }

    @Override
    public Component getTitle() {
        return Component.text("Enchant ").append(Component.translatable(sword));
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < itemsOrder.size(); i++) {
                player.getInventory().setItem(i, itemsOrder.get(i));
            }
        }

        setEnchantmentBlock(Material.ENCHANTING_TABLE.createBlockData());

        game.getEventCanceler().setCancelInventoryEvents(false);
    }

    private void setEnchantmentBlock(BlockData state) {
        game.getConfig().getFloorCenter().add(0, 1, 0).getBlock(game.getWorld()).setBlockData(state);
    }

    @EventHandler
    public void handlePlayerEnchantItemEvent(EnchantItemEvent event) {
        if (!game.getPlayers().contains(event.getEnchanter())) {
            return;
        }

        if (event.getItem().getType().equals(sword)) {
            result.addSuccessfulPlayer(event.getEnchanter());
        } else {
            result.addFailedPlayer(event.getEnchanter());
        }
    }

    @Override
    public void cleanupWorld() {
        setEnchantmentBlock(Material.AIR.createBlockData());
    }
}
