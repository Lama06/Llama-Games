package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EatUntilYouAreFullMiniGame extends CompeteMiniGame<EatUntilYouAreFullMiniGame> {
    private static final Set<Material> FOOD_ITEMS = Set.of(
            Material.BREAD,
            Material.SWEET_BERRIES,
            Material.SALMON,
            Material.COOKED_SALMON,
            Material.POTATO,
            Material.HONEY_BOTTLE,
            Material.MELON_SLICE,
            Material.CARROT,
            Material.COOKED_BEEF,
            Material.DRIED_KELP
    );

    private final List<Material> items;

    public EatUntilYouAreFullMiniGame(LlamaSaysGame game, Consumer<EatUntilYouAreFullMiniGame> callback) {
        super(game, callback);
        items = Util.pickRandomElements(FOOD_ITEMS, 9, game.getRandom());
        game.getEventCanceler().setCancelItemConsummation(false);
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            player.setFoodLevel(0);
            for (int i = 0; i <= 8; i++) {
                player.getInventory().setItem(i, new ItemStack(items.get(i)));
            }
        }
    }

    @Override
    public void handleGameEnded() {
        for (Player player : game.getPlayers()) {
            player.setFoodLevel(20);
        }
    }

    @Override
    public Component getTitle() {
        return Component.text("Eat until you are full");
    }

    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() == 20 && event.getEntity() instanceof Player player) {
            addFailedPlayer(player);
        }
    }
}
