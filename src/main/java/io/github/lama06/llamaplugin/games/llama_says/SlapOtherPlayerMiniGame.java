package io.github.lama06.llamaplugin.games.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class SlapOtherPlayerMiniGame extends MiniGame {
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

    private Material item;

    public SlapOtherPlayerMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public void init() {
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
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getPlayer().getTargetEntity(5) != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == item) {
                result.addSuccessfulPlayer(event.getPlayer());
            } else {
                result.addFailedPlayer(event.getPlayer());
            }
        }
    }
}
