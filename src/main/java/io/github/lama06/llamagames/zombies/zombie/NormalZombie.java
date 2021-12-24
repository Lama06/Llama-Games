package io.github.lama06.llamagames.zombies.zombie;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NormalZombie extends Zombie<NormalZombie> {
    public static final ZombieType<NormalZombie, org.bukkit.entity.Zombie> TYPE = new ZombieType<>(
            ZombieType.SpawnType.WINDOW,
            org.bukkit.entity.Zombie.class,
            NormalZombie::new
    );

    public NormalZombie(ZombiesGame game, ZombieType<NormalZombie, org.bukkit.entity.Zombie> type) {
        super(game, type);
    }

    @Override
    protected int getInitialHealth() {
        return 10;
    }

    @Override
    protected ItemStack getWeapon() {
        return new ItemStack(Material.IRON_SWORD);
    }

    @Override
    protected ItemStack getBoots() {
        return new ItemStack(Material.IRON_BOOTS);
    }

    @Override
    protected ItemStack getLeggins() {
        return new ItemStack(Material.IRON_LEGGINGS);
    }

    @Override
    protected ItemStack getChestPlate() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    @Override
    protected ItemStack getHelmet() {
        return new ItemStack(Material.IRON_HELMET);
    }
}
