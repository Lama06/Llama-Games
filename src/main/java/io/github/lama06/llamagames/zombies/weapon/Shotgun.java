package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Shotgun extends Weapon<Shotgun> {
    public static WeaponType<Shotgun> TYPE = new WeaponType<>(
            "shotgun",
            "Shotgun",
            Shotgun::new
    );

    public Shotgun(ZombiesGame game, ZombiesPlayer player, WeaponType<Shotgun> type) {
        super(game, player, type);
    }

    @Override
    public void initComponents() {
        getComponents().addComponent(new AmmoComponent(90, 6, 50));
        getComponents().addComponent(new AttackCooldownComponent(10));
        getComponents().addComponent(new ItemComponent(new ItemStack(Material.IRON_HOE)));
        getComponents().addComponent(new ShootComponent(7, 20));
    }
}
