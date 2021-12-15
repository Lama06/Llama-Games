package io.github.lama06.lamagames.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Area {
    private BlockPosition position1;
    private BlockPosition position2;

    public Area(BlockPosition position1, BlockPosition position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    // Gson
    public Area() { }

    public int getUpperX() {
        return Math.max(position1.getX(), position2.getX());
    }

    public int getUpperY() {
        return Math.max(position1.getY(), position2.getY());
    }

    public int getUpperZ() {
        return Math.max(position1.getZ(), position2.getZ());
    }

    public int getLowerX() {
        return Math.min(position1.getX(), position2.getX());
    }

    public int getLowerY() {
        return Math.min(position1.getY(), position2.getY());
    }

    public int getLowerZ() {
        return Math.min(position1.getZ(), position2.getZ());
    }

    public BlockPosition getLowerCorner() {
        return new BlockPosition(getLowerX(), getLowerY(), getLowerZ());
    }

    public BlockPosition getUpperCorner() {
        return new BlockPosition(getUpperX(), getUpperY(), getUpperZ());
    }

    public Set<BlockPosition> getBlocks() {
        Set<BlockPosition> blocks = new HashSet<>();

        BlockPosition lowerCorner = getLowerCorner();
        BlockPosition upperCorner = getUpperCorner();

        for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
                for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
                    blocks.add(new BlockPosition(x, y, z));
                }
            }
        }

        return blocks;
    }

    public int getHeight() {
        return getUpperX()-getLowerX() + 1;
    }

    public int getWidthX() {
        return getLowerX()-getLowerY() + 1;
    }

    public int getWidthZ() {
        return getLowerZ()-getUpperZ() + 1;
    }

    public BlockPosition getPosition1() {
        return position1;
    }

    public BlockPosition getPosition2() {
        return position2;
    }

    @Override
    public String toString() {
        return position1 + " " + position2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return Objects.equals(position1, area.position1) && Objects.equals(position2, area.position2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position1, position2);
    }
}
