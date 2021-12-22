package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractWeapon<T extends AbstractWeapon<T>> implements Listener {
    protected ZombiesGame game;
    protected ZombiesPlayer player;
    protected WeaponType<T> type;

    public AbstractWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type) {
        this.game = game;
        this.player = player;
        this.type = type;

        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public void onRemove() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void listenForUse(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(player.getPlayer())) {
            return;
        }

        if (!event.getAction().isLeftClick()) {
            return;
        }

        if (!canUse()) {
            return;
        }

        onUse(event);
    }

    public abstract boolean canUse();

    public abstract void onUse(PlayerInteractEvent event);

    public abstract void editItem(ItemStack item);

    public ItemStack asItem() {
        ItemStack item = new ItemStack(type.getMaterial());
        editItem(item);
        return item;
    }
}
