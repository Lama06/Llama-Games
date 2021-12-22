package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public abstract class AmmoWeapon<T extends AmmoWeapon<T>> extends CooldownWeapon<T> {
    protected int currentMagazineAmmoLeft;
    protected int totalAmmoLeft;
    protected int refillTimeLeft = 0;

    public AmmoWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type) {
        super(game, player, type);
        refillAmmoNow();
    }

    public abstract int getMaxAmmo();

    public abstract int getMaxMagazineAmmo();

    public abstract int getAmmoRefillTime();

    public boolean canRefillAmmo() {
        return totalAmmoLeft > 0;
    }

    public void startRefillAmmo() {
        if (!canRefillAmmo()) {
            return;
        }

        refillTimeLeft = getAmmoRefillTime();
    }

    public void refillAmmoNow() {
        currentMagazineAmmoLeft = getMaxMagazineAmmo();
        totalAmmoLeft = getMaxAmmo()-currentMagazineAmmoLeft;
        player.updateInventory();
    }

    public boolean isRefillingAmmo() {
        return refillTimeLeft != 0;
    }

    @EventHandler
    public void tickRefillAmmoCountdown(ServerTickStartEvent event) {
        if (refillTimeLeft == 1) {
            refillAmmoNow();
            refillTimeLeft = 0;
            player.updateInventory();
        } else if (refillTimeLeft > 0) {
            refillTimeLeft--;
            player.updateInventory();
        }
    }

    @EventHandler
    public void startRefillAmmoOnRightClick(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(player.getPlayer())) {
            return;
        }

        if (!event.getAction().isRightClick()) {
            return;
        }

        if (isRefillingAmmo()) {
            return;
        }

        startRefillAmmo();
    }

    @Override
    public boolean canUse() {
        return super.canUse() && currentMagazineAmmoLeft != 0;
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        super.canUse();

        currentMagazineAmmoLeft--;

        if (currentMagazineAmmoLeft == 0) {
            startRefillAmmo();
        }
    }

    @Override
    public void editItem(ItemStack item) {
        item.setAmount(Math.min(currentMagazineAmmoLeft, item.getType().getMaxStackSize()));

        item.editMeta(Damageable.class, meta -> meta.setDamage((refillTimeLeft / getAmmoRefillTime()) * type.getMaterial().getMaxDurability()));
    }

    public int getCurrentMagazineAmmoLeft() {
        return currentMagazineAmmoLeft;
    }

    public int getTotalAmmoLeft() {
        return totalAmmoLeft;
    }
}
