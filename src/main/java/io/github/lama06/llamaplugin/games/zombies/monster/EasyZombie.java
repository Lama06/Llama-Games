package io.github.lama06.llamaplugin.games.zombies.monster;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.util.EntityPosition;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class EasyZombie extends Monster<EasyZombie, Zombie> {
    public static final MonsterType<EasyZombie, Zombie> TYPE = new MonsterType<>("easy_zombie", MonsterSpawnLocation.WINDOW, Zombie.class, EasyZombie::new);

    public EasyZombie(ZombiesGame game, MonsterType<EasyZombie, Zombie> type, World world, EntityPosition position) {
        super(game, type, world, position);
    }

    @Override
    public void initComponents() {
        components.addComponent(new HealthComponent(10));
        components.addComponent(new MeleeAttackPlayerComponent(2, 3, 30));
        components.addComponent(new PathfinderComponent());
    }

    @Override
    public void onSpawned() {
        entity.setAdult();
        entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    }
}
