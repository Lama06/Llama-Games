package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class RemoveFromInventoryMiniGame extends MiniGame {
    private static final Set<Material> POSSIBLE_ITEMS = Set.of(
            Material.BIRCH_LOG,
            Material.COAL,
            Material.FIRE_CHARGE,
            Material.GOLDEN_APPLE,
            Material.SPONGE
    );

    private Material targetMaterial;

    public RemoveFromInventoryMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        targetMaterial = CollectionUtil.pickRandomElement(POSSIBLE_ITEMS);
        game.getEventCanceler().setCancelItemDrops(false);
        game.getEventCanceler().setCancelInventoryEvents(false);
    }

    @Override
    public Component getTitle() {
        return Component.text("Remove ").append(Component.translatable(targetMaterial)).append(Component.text(" from your inventory"));
    }

    @Override
    public void handleGameStarted() {
        Set<Integer> slots = new HashSet<>(3);
        while (slots.size() < 3) {
            slots.add(game.getRandom().nextInt(36));
        }

        List<Material> items = Arrays.asList(new Material[36]);

        for (int slot : slots) {
            items.set(slot, targetMaterial);
        }

        Set<Material> possibleItems = new HashSet<>(POSSIBLE_ITEMS);
        possibleItems.remove(targetMaterial);

        for (int i = 0; i <= 35; i++) {
            if (items.get(i) != null) {
                continue;
            }

            items.set(i, CollectionUtil.pickRandomElement(possibleItems));
        }

        for (Player player : game.getPlayers()) {
            for (int i = 0; i <= 35; i++) {
                player.getInventory().setItem(i, new ItemStack(items.get(i)));
            }
        }
    }

    @EventHandler
    public void handlePlayerDropItemEvent(PlayerDropItemEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        int numberOfTargetItems = getNumberOfTargetItems(event.getPlayer());

        if (numberOfTargetItems == 0) {
            result.addSuccessfulPlayer(event.getPlayer());
        }
    }

    private int getNumberOfTargetItems(Player player) {
        int result = 0;

        for (int i = 0; i <= 35; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getType() == targetMaterial) {
                result++;
            }
        }

        return result;
    }

    @Override
    public void cleanupWorld() {
        for (Item item : game.getWorld().getEntitiesByClass(Item.class)) {
            item.remove();
        }
    }
}
