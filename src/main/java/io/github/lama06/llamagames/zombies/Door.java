package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import org.bukkit.Material;
import org.bukkit.World;

public class Door {
    public String area1;
    public String area2;
    public int price;
    public BlockPosition activationBlock;
    public BlockArea templateLocation;
    public BlockArea location;

    // Gson
    public Door() { }

    public void open(World world) {
        location.fill(world, Material.AIR.createBlockData());
    }

    public void close(World world) {
        templateLocation.clone(world, location);
    }
}
