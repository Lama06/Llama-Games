package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.util.Named;
import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.GsonConstructor;
import org.bukkit.Material;
import org.bukkit.World;

public class Door implements Named {
    public String name;
    public String area1;
    public String area2;
    public BlockPosition activationBlock;
    public int gold;
    public BlockArea blocks;
    public BlockArea template;

    @GsonConstructor
    public Door() { }

    public Door(String name, String area1, String area2, BlockPosition activationBlock, int gold, BlockArea blocks, BlockArea template) {
        this.name = name;
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
    public String getName() {
        return name;
    }
}
