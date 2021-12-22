package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.weapon.AbstractWeapon;
import io.github.lama06.llamagames.zombies.weapon.AmmoWeapon;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;
import org.bukkit.entity.Player;

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

    public <T extends AbstractWeapon<T>> boolean buyWeapon(WeaponType<T> type, int slot, int gold) {
        if (pay(gold)) {
            giveWeapon(type, slot);
            return true;
        }

        return false;
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
