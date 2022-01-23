package io.github.lama06.llamagames.zombies.monster;

import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.ZombiesGame;
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
        components.addComponent(new EquipmentComponent(new ItemStack(Material.LEATHER_HELMET), null, null, null, null));
        components.addComponent(new MeleeAttackPlayerComponent(1, 3, 40));
        components.addComponent(new PathfinderComponent());
    }

    @Override
    public void onSpawn(Zombie entity) {
        entity.setAdult();
    }
}
