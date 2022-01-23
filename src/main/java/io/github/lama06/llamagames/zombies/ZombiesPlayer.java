package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.zombies.weapon.ItemComponent;
import io.github.lama06.llamagames.zombies.weapon.Weapon;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZombiesPlayer {
    private final ZombiesGame game;
    private final Player player;
    private int health = 20;
    private int gold;
    private final List<Weapon<?>> weapons = new ArrayList<>();

    public ZombiesPlayer(ZombiesGame game, Player player) {
        this.game = game;
        this.player = player;

        weapons.add(null);
        weapons.add(null);
        weapons.add(null);
    }

    public boolean pay(int amount) {
        if (amount <= gold) {
            gold -= amount;
            return true;
        }

        player.sendMessage(Component.text("You can't afford this", NamedTextColor.RED));
        return false;
    }

    public void giveGold(int amount) {
        gold += amount;
    }

    public <T extends Weapon<T>> boolean giveWeapon(WeaponType<T> type) {
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i) == null) {
                weapons.set(i, type.getCreator().createWeapon(game, this, type));
                updateInventory();
                return true;
            }
        }

        return false;
    }

    public boolean hasWeapon(WeaponType<?> type) {
        return weapons.stream().filter(Objects::nonNull).anyMatch(weapon -> weapon.getType().equals(type));
    }

    public void buyWeapon(WeaponShop shop) {
        if (hasWeapon(shop.weapon)) {
            player.sendMessage(Component.text("You already own this weapon", NamedTextColor.RED));
            return;
        }

        if (!pay(shop.gold)) {
            return;
        }

        giveWeapon(shop.weapon);
    }

    public void updateInventory() {
        for (int i = 0; i < weapons.size(); i++) {
            Weapon<?> weapon = weapons.get(i);
            if (weapon == null) {
                player.getInventory().setItem(i, null);
                continue;
            }

            ItemComponent component = weapon.getComponents().getComponent(ItemComponent.class);
            if (component == null) {
                continue;
            }

            player.getInventory().setItem(i, component.item);
        }

        player.setHealth(((double) health / 20D) * player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    public void cleanup() {
        player.getInventory().clear();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
    }

    public int getHealth() {
        return health;
    }

    public void damage(int amount) {
        setHealth(health - amount);
    }

    public void heal(int amount) {
        setHealth(health + amount);
    }

    public void setHealth(int amount) {
        if (amount <= 0) {
            game.killPlayer(player);
            return;
        }

        if (amount > 20) {
            amount = 20;
        }

        health = amount;
        updateInventory();
    }

    public Player getPlayer() {
        return player;
    }

    public List<Weapon<?>> getWeapons() {
        return weapons;
    }

    public Weapon<?> getWeaponInHand() {
        int slot = player.getInventory().getHeldItemSlot();
        if (weapons.size()-1 < slot) {
            return null;
        }
        return weapons.get(slot);
    }
}
