package io.github.lama06.lamagames.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class BlockPosition {
    private int x;
    private int y;
    private int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}
