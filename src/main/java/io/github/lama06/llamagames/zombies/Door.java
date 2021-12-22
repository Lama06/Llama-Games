package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.Area;
import io.github.lama06.llamagames.util.BlockPosition;
import org.bukkit.Material;
import org.bukkit.World;

public class Door {
    public String area1;
    public String area2;
    public int gold;
    public BlockPosition activationBlock;
    public Area templateLocation;
    public Area location;

    // Gson
    public Door() { }

    public void open(World world) {
        location.fill(world, Material.AIR.createBlockData());
    }

    public void close(World world) {
        templateLocation.clone(world, location);
    }
}
