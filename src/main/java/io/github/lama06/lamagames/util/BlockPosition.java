package io.github.lama06.lamagames.util;

import org.bukkit.Location;

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
}
