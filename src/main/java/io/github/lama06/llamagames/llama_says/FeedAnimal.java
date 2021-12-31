package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FeedAnimal extends MiniGame {
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

    public FeedAnimal(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(), callback);
    }

    @Override
    public void init() {
        entityType = Util.pickRandomElement(ENTITY_TYPES_TO_FOOD.keySet(), game.getRandom());
        food = ENTITY_TYPES_TO_FOOD.get(entityType);
    }

    @Override
    public Component getTitle() {
        return Component.text("Feed the ").append(Component.translatable(entityType));
    }

    @Override
    public void handleGameStarted() {
        entity = game.getWorld().spawnEntity(game.getConfig().getFloorCenter().asLocation(game.getWorld()), entityType);

        List<Material> foodItems = Util.pickRandomElements(ENTITY_TYPES_TO_FOOD.values(), 9, game.getRandom());

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < foodItems.size(); i++) {
                player.getInventory().setItem(i, new ItemStack(foodItems.get(i), 1));
            }
        }
    }

    @Override
    public void handleGameEnded() {
        entity.remove();
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        Entity targetEntity = event.getPlayer().getTargetEntity(5);
        if (targetEntity == null || !targetEntity.equals(entity)) {
            return;
        }

        if (event.getPlayer().getInventory().getItemInMainHand().getType() == food) {
            result.addSuccessfulPlayer(event.getPlayer());
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
        } else {
            result.addFailedPlayer(event.getPlayer());
        }
    }
}
