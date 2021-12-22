package io.github.lama06.llamagames.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

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

    public BlockPosition(Location location) {
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
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

    public Distance getDistanceTo(BlockPosition other) {
        Area area = new Area(this, other);
        return new Distance(area.getWidthX(), area.getHeight(), area.getWidthZ());
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
