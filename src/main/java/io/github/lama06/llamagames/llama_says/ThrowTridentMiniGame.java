package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ThrowTridentMiniGame extends CompleteMiniGame<ThrowTridentMiniGame> {
    public ThrowTridentMiniGame(LlamaSaysGame game, Consumer<ThrowTridentMiniGame> callback) {
        super(game, callback);
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
    public void handleGameEnded() {
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
            addSuccessfulPlayer(attacker);
            addFailedPlayer(defender);
        }
    }
}
