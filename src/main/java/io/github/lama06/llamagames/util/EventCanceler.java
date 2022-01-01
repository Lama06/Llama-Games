package io.github.lama06.llamagames.util;

import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.LlamaGamesPlugin;
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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EventCanceler implements Listener {
    private final LlamaGamesPlugin plugin;
    private final Game<?, ?> game;
    private boolean cancelEntityDamage;
    private boolean cancelBlockPlacement;
    private boolean cancelBlockBreaking;
    private boolean cancelEmptyBucket;
    private boolean cancelItemConsummation;
    private boolean cancelInventoryEvents;
    private boolean cancelItemDrops;
    private boolean cancelHunger;

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
        setCancelBlockPlacement(flag);
        setCancelBlockBreaking(flag);
        setCancelItemConsummation(flag);
        setCancelInventoryEvents(flag);
        setCancelItemDrops(flag);
        setCancelHunger(flag);

        setCancelTime(flag);
        setCancelWeather(flag);
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
        if (shouldCancel(event.getPlayer(), cancelBlockPlacement)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelBlockBreakEvent(BlockBreakEvent event) {
        if (shouldCancel(event.getPlayer(), cancelBlockBreaking)) {
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
    private void cancelHunger(FoodLevelChangeEvent event) {
        if (shouldCancel(event.getEntity(), cancelHunger) && event.getEntity().getFoodLevel() > event.getFoodLevel()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void cancelItemDrops(PlayerDropItemEvent event) {
        if (shouldCancel(event.getPlayer(), cancelItemDrops)) {
            event.setCancelled(true);
        }
    }

    public void setCancelEntityDamage(boolean cancelEntityDamage) {
        this.cancelEntityDamage = cancelEntityDamage;
    }

    public void setCancelBlockPlacement(boolean cancelBlockPlacement) {
        this.cancelBlockPlacement = cancelBlockPlacement;
    }

    public void setCancelBlockBreaking(boolean cancelBlockBreaking) {
        this.cancelBlockBreaking = cancelBlockBreaking;
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

    public void setCancelHunger(boolean cancelHunger) {
        this.cancelHunger = cancelHunger;
    }

    public void setCancelTime(boolean cancelTime) {
        game.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !cancelTime);
    }

    public void setCancelWeather(boolean cancelWeather) {
        game.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, !cancelWeather);
    }
}
