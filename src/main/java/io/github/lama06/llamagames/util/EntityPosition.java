package io.github.lama06.llamagames.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public final class EntityPosition {
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public EntityPosition(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public EntityPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityPosition(Location location) {
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        pitch = location.getPitch();
        yaw = location.getYaw();
    }

    @GsonConstructor
    public EntityPosition() { }

    public Distance getDistanceTo(EntityPosition other) {
        double xDistance = Math.max(x, other.x) - Math.min(x, other.x);
        double yDistance = Math.max(y, other.y) - Math.min(y, other.y);
        double zDistance = Math.max(z, other.z) - Math.min(z, other.z);

        return new Distance(xDistance, yDistance, zDistance);
    }

    public Location asLocation(World world) {
        return new Location(world, x, y, z, pitch, yaw);
    }

    public Location asLocation() {
        return asLocation(null);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPosition that = (EntityPosition) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Float.compare(that.pitch, pitch) == 0 && Float.compare(that.yaw, yaw) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, pitch, yaw);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public record Distance(double x, double y, double z) implements Comparable<Distance> {
        @Override
        public int compareTo(EntityPosition.Distance other) {
            return Double.compare(sum(), other.sum());
        }

        public double sum() {
            return x + y + z;
        }
    }
}
