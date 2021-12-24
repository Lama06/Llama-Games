package io.github.lama06.llamagames.zombies.zombie;

import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public abstract class Zombie<T extends Zombie<T>> extends PathfinderZombie<T, org.bukkit.entity.Zombie> {
    public Zombie(ZombiesGame game, ZombieType<T, org.bukkit.entity.Zombie> type) {
        super(game, type);
    }

    @Override
    public void onSpawn(org.bukkit.entity.Zombie entity) {
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(getWeapon());
        equipment.setBoots(getBoots());
        equipment.setLeggings(getLeggins());
        equipment.setChestplate(getChestPlate());
        equipment.setHelmet(getHelmet());
    }

    @Override
    protected boolean canAttack(Player player) {
        EntityPosition.Distance distanceTo = new EntityPosition(player.getLocation()).getDistanceTo(new EntityPosition(entity.getLocation()));
        return distanceTo.y() < 1 && distanceTo.x() < 2 && distanceTo.z() < 2;
    }

    @Override
    protected void attack(Player player) {
        entity.attack(player);
    }

    protected abstract ItemStack getWeapon();

    protected abstract ItemStack getBoots();

    protected abstract ItemStack getLeggins();

    protected abstract ItemStack getChestPlate();

    protected abstract ItemStack getHelmet();
}
