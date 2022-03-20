package io.github.lama06.llamaplugin.games.llama_says;

import io.github.lama06.llamaplugin.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FeedAnimalMiniGame extends MiniGame {
    private static final Map<EntityType, Material> ENTITY_TYPES_TO_FOOD = Map.ofEntries(
            Map.entry(EntityType.SHEEP, Material.WHEAT),
            Map.entry(EntityType.PIG, Material.CARROT),
            Map.entry(EntityType.COW, Material.WHEAT),
            Map.entry(EntityType.PANDA, Material.CAKE),
            Map.entry(EntityType.PARROT, Material.WHEAT_SEEDS),
            Map.entry(EntityType.CHICKEN, Material.WHEAT_SEEDS)
    );

    private EntityType entityType;
    private Material food;
    private Entity entity;

    public FeedAnimalMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(game), callback);
    }

    @Override
    public void init() {
        entityType = CollectionUtil.pickRandomElement(ENTITY_TYPES_TO_FOOD.keySet(), game.getRandom());
        food = ENTITY_TYPES_TO_FOOD.get(entityType);
    }

    @Override
    public Component getTitle() {
        return Component.text("Drop the ").append(Component.translatable(entityType)).append(Component.text(" food"));
    }

    @Override
    public void handleGameStarted() {
        entity = game.getWorld().spawnEntity(game.getConfig().getFloorCenter().asLocation(game.getWorld()).add(0, 1, 0), entityType);

        List<Material> foodItems = CollectionUtil.pickRandomElements(ENTITY_TYPES_TO_FOOD.values(), 9, game.getRandom());

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < foodItems.size(); i++) {
                player.getInventory().setItem(i, new ItemStack(foodItems.get(i)));
            }
        }

        game.getEventCanceler().setCancelItemDrops(false);
        game.getEventCanceler().setCancelInventoryEvents(false);
    }

    @Override
    public void cleanupWorld() {
        entity.remove();
    }

    @EventHandler
    public void handlePlayerItemDropEvent(PlayerDropItemEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        Entity targetEntity = event.getPlayer().getTargetEntity(5);;
        if (targetEntity == null || !targetEntity.equals(entity)) {
            return;
        }

        if (event.getItemDrop().getItemStack().getType() == food) {
            result.addSuccessfulPlayer(event.getPlayer());
        } else {
            result.addFailedPlayer(event.getPlayer());
        }
    }
}
