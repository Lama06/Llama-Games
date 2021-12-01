package io.github.lama06.lamagames.lama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class SlapOtherPlayerMiniGame extends CompeteMiniGame<SlapOtherPlayerMiniGame> {
    private static final List<Material> ITEMS = List.of(
            Material.MELON,
            Material.COOKIE,
            Material.APPLE,
            Material.SWEET_BERRIES,
            Material.GLOW_BERRIES,
            Material.HONEY_BOTTLE,
            Material.CAKE,
            Material.AXOLOTL_BUCKET,
            Material.BAKED_POTATO
    );

    private final Material item;

    public SlapOtherPlayerMiniGame(LamaSaysGame game, Consumer<SlapOtherPlayerMiniGame> callback) {
        super(game, callback);

        item = ITEMS.get(game.getRandom().nextInt(ITEMS.size()));
    }

    @Override
    public Component getTitle() {
        return Component.text("Slap a player with: ").append(Component.translatable(item));
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            for (int i = 0; i <= 8; i++) {
                player.getInventory().setItem(i, new ItemStack(ITEMS.get(i)));
            }
        }
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().equals(game.getWorld()) || !game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getPlayer().getTargetEntity(5) != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == item) {
                addSuccessfulPlayer(event.getPlayer());
            } else {
                addFailedPlayer(event.getPlayer());
            }
        }
    }
}
