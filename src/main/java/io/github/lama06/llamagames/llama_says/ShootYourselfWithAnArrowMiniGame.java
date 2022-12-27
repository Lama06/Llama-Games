package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ShootYourselfWithAnArrowMiniGame extends MiniGame {
    public ShootYourselfWithAnArrowMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(game), callback);
    }

    @Override
    public Component getTitle() {
        return Component.text("Shoot yourself with an arrow");
    }

    @Override
    public void handleGameStarted() {
        for (Player player : game.getPlayers()) {
            player.getInventory().setItem(game.getRandom().nextInt(9), new ItemStack(Material.BOW));
            player.getInventory().setItemInOffHand(new ItemStack(Material.ARROW, 64));
        }
    }

    @EventHandler
    public void handleProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof Player player &&
                event.getEntity() instanceof Arrow arrow &&
                player.equals(arrow.getShooter())) {
            result.addSuccessfulPlayer(player);
        }
    }

    @Override
    public void cleanupPlayer(Player player) {
        player.setArrowsInBody(0);
    }

    @Override
    public void cleanupWorld() {
        for (Arrow arrow : game.getWorld().getEntitiesByClass(Arrow.class)) {
            arrow.remove();
        }
    }
}
