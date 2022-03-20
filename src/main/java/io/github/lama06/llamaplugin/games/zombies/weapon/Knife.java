package io.github.lama06.llamaplugin.games.zombies.weapon;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.games.zombies.ZombiesPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Knife extends Weapon<Knife> {
    public static final WeaponType<Knife> TYPE = new WeaponType<>(
            "knife",
            "Knife",
            Knife::new
    );

    public Knife(ZombiesGame game, ZombiesPlayer player, WeaponType<Knife> type) {
        super(game, player, type);
    }

    @Override
    public void initComponents() {
        getComponents().addComponent(new AttackCooldownComponent(30));
        getComponents().addComponent(new MeleeComponent(5, 3));
        getComponents().addComponent(new ItemComponent(new ItemStack(Material.IRON_SWORD)));
    }
}
