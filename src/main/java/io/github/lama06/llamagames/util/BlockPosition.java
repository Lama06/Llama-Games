package io.github.lama06.llamagames.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BlockPosition {
    private int x;
    private int y;
    private int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(Location location) {
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
    }

    public BlockPosition(Block block) {
        this(block.getLocation());
    }

    // Gson
    public BlockPosition() { }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location asLocation() {
        return new Location(null, x, y, z);
    }

    public Location asLocation(World world) {
        return new Location(world, x, y, z);
    }

    public EntityPosition asEntityPosition() {
        return new EntityPosition(x, y, z);
    }

    public Distance getDistanceTo(BlockPosition other) {
        BlockArea area = new BlockArea(this, other);
        return new Distance(area.getWidthX(), area.getHeight(), area.getWidthZ());
    }

    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(this.x+x, this.y+y, this.z+z);
    }

    public BlockPosition getRelative(BlockFace face) {
        return new BlockPosition(x+face.getModX(), y+face.getModY(), z+face.getModZ());
    }

    public Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosition that = (BlockPosition) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    
    public record Distance(int x, int y, int z) implements Comparable<Distance> {
        @Override
        public int compareTo(@NotNull BlockPosition.Distance other) {
            return Integer.compare(sum(), other.sum());
        }

        public int sum() {
            return x + y + z;
        }
    }
}
