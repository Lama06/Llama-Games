package io.github.lama06.llamaplugin.games.zombies.monster;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.util.EntityPosition;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class HardZombie extends Monster<HardZombie, Zombie> {
    public static final MonsterType<HardZombie, Zombie> TYPE = new MonsterType<>(
            "hard_zombie",
            MonsterSpawnLocation.WINDOW,
            Zombie.class,
            HardZombie::new
    );

    public HardZombie(ZombiesGame game, MonsterType<HardZombie, Zombie> type, World world, EntityPosition position) {
        super(game, type, world, position);
    }

    @Override
    public void initComponents() {
        components.addComponent(new HealthComponent(25));
        components.addComponent(new MeleeAttackPlayerComponent(7, 3, 40));
        components.addComponent(new PathfinderComponent());
    }

    @Override
    public void onSpawned() {
        entity.setAdult();

        entity.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        entity.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        entity.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        entity.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
    }
}
