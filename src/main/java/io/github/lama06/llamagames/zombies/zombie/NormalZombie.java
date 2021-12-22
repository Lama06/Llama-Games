package io.github.lama06.llamagames.zombies.zombie;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class NormalZombie extends AbstractZombie<NormalZombie, Zombie> {
    public NormalZombie(ZombiesGame game, ZombieType<NormalZombie, Zombie> type) {
        super(game, type);
    }

    @Override
    public void onSpawn(Zombie entity) {
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD), true);
    }

    @Override
    public int getInitialHealth() {
        return 20;
    }
}
