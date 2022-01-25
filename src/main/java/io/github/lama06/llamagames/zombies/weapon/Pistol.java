package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Pistol extends Weapon<Pistol> {
    public static final WeaponType<Pistol> TYPE = new WeaponType<>(
            "pistol",
            "Pistol",
            Pistol::new
    );

    public Pistol(ZombiesGame game, ZombiesPlayer player, WeaponType<Pistol> type) {
        super(game, player, type);
    }

    @Override
    public void initComponents() {
        getComponents().addComponent(new AmmoComponent(200, 20, 20));
        getComponents().addComponent(new AttackCooldownComponent(3));
        getComponents().addComponent(new ItemComponent(new ItemStack(Material.STONE_HOE)));
        getComponents().addComponent(new ShootComponent(15, 4));
    }
}
