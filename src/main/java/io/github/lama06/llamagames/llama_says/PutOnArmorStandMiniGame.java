package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PutOnArmorStandMiniGame extends MiniGame {
    private static final Set<Material> ITEMS = Set.of(
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_BOOTS,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_HELMET,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE
    );

    private Material item;
    private ArmorStand armorStand;

    public PutOnArmorStandMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        item = CollectionUtil.pickRandomElement(ITEMS, game.getRandom());
    }

    @Override
    public Component getTitle() {
        return Component.text("Put the item on the armor stand: ").append(Component.translatable(item));
    }

    @Override
    public void handleGameStarted() {
        armorStand = game.getWorld().spawn(game.getConfig().getFloorCenter().asLocation(game.getWorld()).add(0, 1, 0), ArmorStand.class);

        List<Material> items = CollectionUtil.pickRandomElements(ITEMS, 9, game.getRandom());

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < items.size(); i++) {
                player.getInventory().setItem(i, new ItemStack(items.get(i)));
            }
        }
    }

    @EventHandler
    public void handlePlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getPlayerItem().getType() == item) {
            result.addSuccessfulPlayer(event.getPlayer());
        } else {
            result.addFailedPlayer(event.getPlayer());
        }

        event.setCancelled(true);
    }

    @Override
    public void cleanupWorld() {
        armorStand.remove();
    }
}
