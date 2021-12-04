package io.github.lama06.lamagames.util;

import io.github.lama06.lamagames.Game;
import io.github.lama06.lamagames.LamaGamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EventCanceler implements Listener {
    private final LamaGamesPlugin plugin;
    private final Game<?, ?> game;
    private boolean cancelEntityDamageByEntity;
    private boolean cancelEntityDamageByBlock;
    private boolean cancelBlockPlacement;
    private boolean cancelBlockBreaking;
    private boolean cancelItemConsummation;
    private boolean cancelHunger;

    public EventCanceler(LamaGamesPlugin plugin, Game<?, ?> game) {
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
        cancelEntityDamageByEntity = flag;
        cancelEntityDamageByBlock = flag;
        cancelBlockPlacement = flag;
        cancelBlockBreaking = flag;
        cancelItemConsummation = flag;
        cancelHunger = flag;
    }

    public void allowAll() {
        setAllFlags(false);
    }

    public void disallowAll() {
        setAllFlags(true);
    }

    private boolean shouldCancel(World world) {
        return world.equals(game.getWorld()) && game.getConfig().cancelEvents;
    }

    @EventHandler
    public void cancelEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (cancelEntityDamageByEntity && shouldCancel(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelEntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
        if (cancelEntityDamageByBlock && shouldCancel(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelBlockPlaceEvent(BlockPlaceEvent event) {
        if (cancelBlockPlacement && shouldCancel(event.getPlayer().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelBlockBreakEvent(BlockBreakEvent event) {
        if (cancelBlockBreaking && shouldCancel(event.getPlayer().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelItemConsummation(PlayerItemConsumeEvent event) {
        if (cancelItemConsummation && shouldCancel(event.getPlayer().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelHunger(FoodLevelChangeEvent event) {
        if (cancelHunger && shouldCancel(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }

    public void setCancelEntityDamageByEntity(boolean cancelEntityDamageByEntity) {
        this.cancelEntityDamageByEntity = cancelEntityDamageByEntity;
    }

    public void setCancelEntityDamageByBlock(boolean cancelEntityDamageByBlock) {
        this.cancelEntityDamageByBlock = cancelEntityDamageByBlock;
    }

    public void setCancelBlockPlacement(boolean cancelBlockPlacement) {
        this.cancelBlockPlacement = cancelBlockPlacement;
    }

    public void setCancelBlockBreaking(boolean cancelBlockBreaking) {
        this.cancelBlockBreaking = cancelBlockBreaking;
    }

    public void setCancelItemConsummation(boolean cancelItemConsummation) {
        this.cancelItemConsummation = cancelItemConsummation;
    }
}
