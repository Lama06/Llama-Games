package io.github.lama06.llamaplugin.games.llama_says;

import io.github.lama06.llamaplugin.util.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class DoNotGetKilledByTntMiniGame extends MiniGame {
    private BukkitTask task;

    public DoNotGetKilledByTntMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new CompleteResult(game), callback);
    }

    @Override
    public void cleanup() {
        task.cancel();
    }

    @Override
    public void cleanupWorld() {
        for (TNTPrimed tnt : game.getWorld().getEntitiesByClass(TNTPrimed.class)) {
            tnt.remove();
        }
    }

    @Override
    public Component getTitle() {
        return Component.text("Don't get killed by the TNT");
    }

    @Override
    public void handleGameStarted() {
        task = Bukkit.getScheduler().runTaskTimer(game.getModule().getPlugin(), this::spawnTnt, 40, 20);

        for (Player player : game.getPlayers()) {
            player.setHealth(1);
        }
    }

    private void spawnTnt() {
        BlockPosition position = game.getConfig().getFloor().pickRandomBlock(game.getRandom());
        game.getWorld().spawn(position.asLocation(game.getWorld()).add(0, 10, 0), TNTPrimed.class, tnt -> tnt.setFuseTicks(80));
    }

    @EventHandler(ignoreCancelled = true)
    public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !game.getPlayers().contains(player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            result.addFailedPlayer(player);
        }
    }

    @Override
    public void handleGameEnded() {
        for (Player player : game.getPlayers()) {
            result.addSuccessfulPlayer(player);
        }
    }
}
