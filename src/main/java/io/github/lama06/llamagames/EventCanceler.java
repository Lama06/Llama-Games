package io.github.lama06.llamagames;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EventCanceler implements Listener {
    private final LlamaGamesPlugin plugin;
    private final Game<?, ?> game;
    private boolean cancelEntityDamage;
    private boolean cancelPlayerBlockPlacement;
    private boolean cancelPlayerBlockBreaking;
    private boolean cancelEntityExplosions;
    private boolean cancelEmptyBucket;
    private boolean cancelItemConsummation;
    private boolean cancelInventoryEvents;
    private boolean cancelItemDrops;
    private boolean cancelFoodLevelChange;

    public EventCanceler(LlamaGamesPlugin plugin, Game<?, ?> game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    private void setAllFlags(boolean flag) {
        setCancelEntityDamage(flag);
        setCancelPlayerBlockPlacement(flag);
        setCancelPlayerBlockBreaking(flag);
        setCancelEntityExplosions(flag);
        setCancelEmptyBucket(flag);
        setCancelItemConsummation(flag);
        setCancelInventoryEvents(flag);
        setCancelItemDrops(flag);
        setCancelFoodLevelChange(flag);

        setCancelTime(flag);
        setCancelWeather(flag);
        setCancelNaturalMobSpawns(flag);
    }

    public void allowAll() {
        setAllFlags(false);
    }

    public void disallowAll() {
        setAllFlags(true);
    }

    private boolean shouldCancel(World world, boolean flag) {
        if (flag) {
            return world.equals(game.getWorld()) && game.getConfig().isCancelEvents();
        } else {
            return false;
        }
    }

    private boolean shouldCancel(Player player, boolean flag) {
        if (flag) {
            if (!player.getWorld().equals(game.getWorld())) {
                return false;
            }

            if (!game.getConfig().isCancelEvents()) {
                return false;
            }

            if (player.isOp() && game.getConfig().isDoNotCancelOpEvents() && !game.isRunning()) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean shouldCancel(Entity entity, boolean flag) {
        if (entity instanceof Player player) {
            return shouldCancel(player, flag);
        }

        return shouldCancel(entity.getWorld(), flag);
    }

    @EventHandler
    private void handleEntityDamageEvent(EntityDamageEvent event) {
        if (shouldCancel(event.getEntity(), cancelEntityDamage)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelBlockPlaceEvent(BlockPlaceEvent event) {
        if (shouldCancel(event.getPlayer(), cancelPlayerBlockPlacement)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelBlockBreakEvent(BlockBreakEvent event) {
        if (shouldCancel(event.getPlayer(), cancelPlayerBlockBreaking)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelEmptyBucketEvent(PlayerBucketEmptyEvent event) {
        if (shouldCancel(event.getPlayer(), cancelEmptyBucket)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelItemConsummation(PlayerItemConsumeEvent event) {
        if (shouldCancel(event.getPlayer(), cancelItemConsummation)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelInventoryEvents(InventoryClickEvent event) {
        if (shouldCancel((Player) event.getWhoClicked(), cancelInventoryEvents)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelFoodLevelChange(FoodLevelChangeEvent event) {
        if (shouldCancel(event.getEntity(), cancelFoodLevelChange)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelItemDrops(PlayerDropItemEvent event) {
        if (shouldCancel(event.getPlayer(), cancelItemDrops)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelEntityExplosions(EntityExplodeEvent event) {
        if (shouldCancel(event.getEntity(), cancelEntityExplosions)) {
            event.blockList().clear();
        }
    }

    public void setCancelEntityDamage(boolean cancelEntityDamage) {
        this.cancelEntityDamage = cancelEntityDamage;
    }

    public void setCancelPlayerBlockPlacement(boolean cancelPlayerBlockPlacement) {
        this.cancelPlayerBlockPlacement = cancelPlayerBlockPlacement;
    }

    public void setCancelPlayerBlockBreaking(boolean cancelPlayerBlockBreaking) {
        this.cancelPlayerBlockBreaking = cancelPlayerBlockBreaking;
    }

    public void setCancelEntityExplosions(boolean cancelEntityExplosions) {
        this.cancelEntityExplosions = cancelEntityExplosions;
    }

    public void setCancelEmptyBucket(boolean cancelEmptyBucket) {
        this.cancelEmptyBucket = cancelEmptyBucket;
    }

    public void setCancelItemConsummation(boolean cancelItemConsummation) {
        this.cancelItemConsummation = cancelItemConsummation;
    }

    public void setCancelInventoryEvents(boolean cancelInventoryEvents) {
        this.cancelInventoryEvents = cancelInventoryEvents;
    }

    public void setCancelItemDrops(boolean cancelItemDrops) {
        this.cancelItemDrops = cancelItemDrops;
    }

    public void setCancelFoodLevelChange(boolean cancelFoodLevelChange) {
        this.cancelFoodLevelChange = cancelFoodLevelChange;
    }

    public void setCancelTime(boolean cancelTime) {
        game.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !cancelTime);
    }

    public void setCancelWeather(boolean cancelWeather) {
        game.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, !cancelWeather);
    }

    public void setCancelNaturalMobSpawns(boolean cancelNaturalMobSpawns) {
        game.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, !cancelNaturalMobSpawns);
    }
}
