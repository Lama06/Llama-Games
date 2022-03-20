package io.github.lama06.llamaplugin.games.llama_says;

import io.github.lama06.llamaplugin.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EatUntilYouAreFullMiniGame extends MiniGame {
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

    private List<Material> items;

    public EatUntilYouAreFullMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
        items = CollectionUtil.pickRandomElements(FOOD_ITEMS, 9, game.getRandom());
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            player.setFoodLevel(0);
            for (int i = 0; i <= 8; i++) {
                player.getInventory().setItem(i, new ItemStack(items.get(i)));
            }
        }

        game.getEventCanceler().setCancelItemConsummation(false);
        game.getEventCanceler().setCancelFoodLevelChange(false);
    }

    @Override
    public Component getTitle() {
        return Component.text("Eat until you are full");
    }

    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() == 20 && event.getEntity() instanceof Player player) {
            result.addSuccessfulPlayer(player);
        }
    }
}
