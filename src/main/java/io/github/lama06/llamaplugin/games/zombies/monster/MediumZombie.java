package io.github.lama06.llamaplugin.games.zombies.monster;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.util.EntityPosition;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class MediumZombie extends Monster<MediumZombie, Zombie> {
    public static final MonsterType<MediumZombie, Zombie> TYPE = new MonsterType<>(
            "medium_zombie",
            MonsterSpawnLocation.WINDOW,
            Zombie.class,
            MediumZombie::new
    );

    public MediumZombie(ZombiesGame game, MonsterType<MediumZombie, Zombie> type, World world, EntityPosition position) {
        super(game, type, world, position);
    }

    @Override
    public void initComponents() {
        components.addComponent(new HealthComponent(20));
        components.addComponent(new MeleeAttackPlayerComponent(4, 4, 30));
        components.addComponent(new PathfinderComponent());
    }

    @Override
    public void onSpawned() {
        entity.setAdult();

        entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        entity.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        entity.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
    }
}
