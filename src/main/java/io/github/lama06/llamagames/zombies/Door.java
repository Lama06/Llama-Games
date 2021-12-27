package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Objects;

public class Door {
    public String name;
    public String area1;
    public String area2;
    public int price;
    public BlockArea location;
    public BlockArea templateLocation;
    public BlockPosition activationBlock;

    // Gson
    public Door() { }

    public Door(String name, String area1, String area2, int price, BlockArea location, BlockArea templateLocation, BlockPosition activationBlock) {
        this.name = name;
        this.area1 = area1;
        this.area2 = area2;
        this.price = price;
        this.location = location;
        this.templateLocation = templateLocation;
        this.activationBlock = activationBlock;
    }

    public void open(World world) {
        location.fill(world, Material.AIR.createBlockData());
    }

    public void close(World world) {
        templateLocation.clone(world, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Door door = (Door) o;
        return Objects.equals(name, door.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
