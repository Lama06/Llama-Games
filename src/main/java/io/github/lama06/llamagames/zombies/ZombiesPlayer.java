package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.weapon.AbstractWeapon;
import io.github.lama06.llamagames.zombies.weapon.AmmoWeapon;
import io.github.lama06.llamagames.zombies.weapon.WeaponShop;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ZombiesPlayer {
    private final ZombiesGame game;
    private final Player player;
    private int gold;
    private final AbstractWeapon<?>[] weapons = new AbstractWeapon<?>[3];
    private int kills;

    public ZombiesPlayer(ZombiesGame game, Player player) {
        this.game = game;
        this.player = player;
    }

    public boolean pay(int amount) {
        if (amount > gold) {
            return false;
        }

        gold -= amount;
        return true;
    }

    public void updateInventory() {
        for (int i = 0; i < weapons.length; i++) {
            AbstractWeapon<?> weapon = weapons[i];
            player.getInventory().setItem(i, weapon.asItem());
        }

        int slot = player.getInventory().getHeldItemSlot();
        if (weapons.length > slot && weapons[slot] instanceof AmmoWeapon<?> weapon) {
            player.setLevel(weapon.getTotalAmmoLeft());
        }
    }

    public <T extends AbstractWeapon<T>> void giveWeapon(WeaponType<T> type, int slot) {
        T weapon = type.getCreator().createWeapon(game, this, type);
        weapons[slot] = weapon;
        updateInventory();
    }

    public Optional<Integer> getWeaponSlot(WeaponType<?> type) {
        for (int i = 0; i < weapons.length; i++) {
            AbstractWeapon<?> weapon = weapons[i];

            if (weapon.getType().equals(type)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public boolean hasWeapon(WeaponType<?> type) {
        return getWeaponSlot(type).isPresent();
    }

    public void onWeaponShopInteraction(WeaponShop shop, int slot) {
        Optional<Integer> weaponSlot = getWeaponSlot(shop.weapon);

        if (weaponSlot.isEmpty()) {
            if (pay(gold)) {
                giveWeapon(shop.weapon, slot);
                player.sendMessage(Component.text("Successfully bought the weapon", NamedTextColor.GREEN));
                return;
            }

            player.sendMessage(Component.text("You cannot afford this", NamedTextColor.RED));
        } else {
            if (weapons[weaponSlot.get()] instanceof AmmoWeapon<?> ammoWeapon && pay(shop.refillPrice)) {
                ammoWeapon.restockAmmo();
            } else {
                player.sendMessage(Component.text("You cannot refill this weapon", NamedTextColor.RED));
            }
        }
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        kills++;
    }

    public Player getPlayer() {
        return player;
    }
}
