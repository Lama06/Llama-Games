package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Rifle extends Weapon<Rifle> {
    public static final WeaponType<Rifle> TYPE = new WeaponType<>(
            "rifle",
            "Rifle",
            Rifle::new
    );

    public Rifle(ZombiesGame game, ZombiesPlayer player, WeaponType<Rifle> type) {
        super(game, player, type);
    }

    @Override
    public void initComponents() {
        getComponents().addComponent(new AmmoComponent(300, 30, 40));
        getComponents().addComponent(new AttackCooldownComponent(4));
        getComponents().addComponent(new ItemComponent(new ItemStack(Material.STONE_HOE)));
        getComponents().addComponent(new ShootComponent(20, 5));
    }
}
