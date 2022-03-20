package io.github.lama06.llamaplugin.games.llama_says;

import com.destroystokyo.paper.MaterialTags;
import io.github.lama06.llamaplugin.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AddEnchantmentMiniGame extends MiniGame {
    private static final Set<Material> ITEMS = CollectionUtil.combineSets(
            MaterialTags.SWORDS.getValues(),
            MaterialTags.AXES.getValues()
    );

    private static final Set<Enchantment> ENCHANTMENTS = Set.of(
            Enchantment.DURABILITY,
            Enchantment.DAMAGE_ALL, // Sharpness
            Enchantment.DAMAGE_ARTHROPODS,
            Enchantment.DAMAGE_UNDEAD // Smite
    );

    private Material item;
    private Enchantment enchantment;
    private List<ItemStack> itemsOrder;

    public AddEnchantmentMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        item = CollectionUtil.pickRandomElement(ITEMS, game.getRandom());
        enchantment = CollectionUtil.pickRandomElement(ENCHANTMENTS, game.getRandom());

        Set<ItemStack> items = ITEMS.stream().map(ItemStack::new).collect(Collectors.toSet());

        for (Enchantment enchantment : ENCHANTMENTS) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            book.editMeta(EnchantmentStorageMeta.class, meta -> meta.addStoredEnchant(enchantment, 1, true));
            items.add(book);
        }

        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 64));

        itemsOrder = CollectionUtil.pickRandomElements(items, -1, game.getRandom());
    }

    @Override
    public Component getTitle() {
        return Component.text("Add ")
                .append(Component.translatable(enchantment))
                .append(Component.text(" to "))
                .append(Component.translatable(item));
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < itemsOrder.size(); i++) {
                player.getInventory().setItem(i, itemsOrder.get(i));
            }
        }

        setAnvilBLock(Material.ANVIL.createBlockData());

        game.getEventCanceler().setCancelInventoryEvents(false);
    }

    private void setAnvilBLock(BlockData state) {
        game.getConfig().getFloorCenter().add(0, 1, 0).getBlock(game.getWorld()).setBlockData(state);
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !game.getPlayers().contains(player)) {
            return;
        }

        if (!(event.getInventory() instanceof AnvilInventory anvil) || event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        ItemStack firstItem = anvil.getFirstItem();
        boolean firstCorrect = firstItem != null && firstItem.getType() == item;

        ItemStack secondItem = anvil.getSecondItem();
        boolean secondCorrect = secondItem != null && secondItem.getItemMeta() instanceof EnchantmentStorageMeta meta && meta.hasStoredEnchant(enchantment);

        if (firstCorrect && secondCorrect) {
            result.addSuccessfulPlayer(player);
        } else {
            result.addFailedPlayer(player);
        }
    }

    @Override
    public void cleanupWorld() {
        setAnvilBLock(Material.AIR.createBlockData());
    }
}
