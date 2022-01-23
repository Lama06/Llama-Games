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
    public BlockPosition activationBlock;
    public int gold;
    public BlockArea blocks;
    public BlockArea template;

    // Gson
    public Door() { }

    public Door(String name, String area1, String area2, BlockPosition activationBlock, int gold, BlockArea blocks, BlockArea template) {
        this.area1 = area1;
        this.area2 = area2;
        this.activationBlock = activationBlock;
        this.gold = gold;
        this.blocks = blocks;
        this.template = template;
    }

    public void open(World world) {
        blocks.fill(world, Material.AIR.createBlockData());
    }

    public void close(World world) {
        template.clone(world, blocks);
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
