package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ThrowTridentMiniGame extends MiniGame {
    public ThrowTridentMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(game), callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Throw a trident at another player");
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.TRIDENT));
        }
    }

    @Override
    public void cleanupWorld() {
        for (Trident trident : game.getWorld().getEntitiesByClass(Trident.class)) {
            trident.remove();
        }
    }

    @EventHandler
    public void handleProjectileHitEvent(ProjectileHitEvent event) {
        if (!event.getEntity().getWorld().equals(game.getWorld()) || !(event.getEntity() instanceof Trident trident)) {
            return;
        }

        if (event.getHitEntity() != null &&
                event.getHitEntity() instanceof Player defender &&
                trident.getShooter() != null &&
                trident.getShooter() instanceof Player attacker) {
            result.addSuccessfulPlayer(attacker);
            result.addFailedPlayer(defender);
        }
    }
}
