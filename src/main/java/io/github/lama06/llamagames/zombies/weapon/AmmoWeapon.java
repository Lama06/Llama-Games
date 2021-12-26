package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.entity.Player;
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
        restockAmmo();
    }

    public abstract int getMaxAmmo();

    public abstract int getMaxMagazineAmmo();

    public abstract int getAmmoRefillTime();

    public void restockAmmo() {
        refillTimeLeft = 0;
        totalAmmoLeft = getMaxAmmo();
        refillAmmoNow();
    }

    public boolean canRefillAmmo() {
        return totalAmmoLeft > 0;
    }

    public void startRefillAmmo() {
        if (!canRefillAmmo()) {
            return;
        }

        refillTimeLeft = getAmmoRefillTime();
    }

    public boolean isRefillingAmmo() {
        return refillTimeLeft != 0;
    }

    public void stopRefillingAmmo() {
        refillTimeLeft = 0;
    }

    public void refillAmmoNow() {
        if (!canRefillAmmo()) {
            return;
        }

        int ammoToRefill = getMaxMagazineAmmo();
        if (ammoToRefill > totalAmmoLeft) {
            ammoToRefill = totalAmmoLeft;
        }

        currentMagazineAmmoLeft = ammoToRefill;
        totalAmmoLeft = totalAmmoLeft-ammoToRefill;

        player.updateInventory();
    }

    @EventHandler
    private void tickRefillAmmoCountdown(ServerTickStartEvent event) {
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
    private void startRefillAmmoOnRightClick(PlayerInteractEvent event) {
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

    @EventHandler
    private void useTotalAmmoLeftAsLevelBar(ServerTickStartEvent event) {
        Player player = this.player.getPlayer();

        if (player.getLevel() != totalAmmoLeft) {
            player.setLevel(totalAmmoLeft);
        }
    }

    @EventHandler
    private void useMagazineAmmoLeftAsXp(ServerTickStartEvent event) {
        Player player = this.player.getPlayer();

        float magazineAmmoAsXp = (float) currentMagazineAmmoLeft / (float) getMaxMagazineAmmo();

        if (player.getExp() != magazineAmmoAsXp) {
            player.setExp(magazineAmmoAsXp);
        }
    }

    @Override
    public boolean canUse() {
        return super.canUse() && currentMagazineAmmoLeft != 0 && !isRefillingAmmo();
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
        item.editMeta(Damageable.class, meta -> meta.setDamage((refillTimeLeft / getAmmoRefillTime()) * type.getMaterial().getMaxDurability()));
    }

    public int getCurrentMagazineAmmoLeft() {
        return currentMagazineAmmoLeft;
    }

    public int getTotalAmmoLeft() {
        return totalAmmoLeft;
    }
}
