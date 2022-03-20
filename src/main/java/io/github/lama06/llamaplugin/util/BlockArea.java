package io.github.lama06.llamaplugin.util;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.*;

public class BlockArea {
    private BlockPosition position1;
    private BlockPosition position2;

    public BlockArea(BlockPosition position1, BlockPosition position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    @GsonConstructor
    public BlockArea() { }

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

    public boolean containsBlock(BlockPosition position) {
        BlockPosition lowerCorner = getLowerCorner();
        BlockPosition upperCorner = getUpperCorner();

        boolean x = position.getX() >= lowerCorner.getX() && position.getX() <= upperCorner.getX();
        boolean y = position.getY() >= lowerCorner.getY() && position.getY() <= upperCorner.getY();
        boolean z = position.getZ() >= lowerCorner.getZ() && position.getZ() <= upperCorner.getZ();

        return x && y && z;
    }

    public void clone(World world, BlockArea destination) {
        if (!hasSameDimensions(destination)) {
            return;
        }

        BlockPosition sourceLowerCorner = getLowerCorner();
        BlockPosition sourceUpperCorner = getUpperCorner();
        BlockPosition destinationLowerCorner = destination.getLowerCorner();

        for (int x = sourceLowerCorner.getX(); x <= sourceUpperCorner.getX(); x++) {
            for (int y = sourceLowerCorner.getY(); y <= sourceUpperCorner.getY(); y++) {
                for (int z = sourceLowerCorner.getZ(); z <= sourceUpperCorner.getZ(); z++) {
                    int xOffset = x-sourceLowerCorner.getX();
                    int yOffset = y-sourceLowerCorner.getY();
                    int zOffset = z-sourceLowerCorner.getZ();

                    world.getBlockAt(
                            destinationLowerCorner.getX()+xOffset,
                            destinationLowerCorner.getY()+yOffset,
                            destinationLowerCorner.getZ()+zOffset
                    ).setBlockData(world.getBlockData(x, y, z));
                }
            }
        }
    }

    public void fill(World world, BlockData data) {
        for (BlockPosition block : getBlocks()) {
            world.getBlockAt(block.asLocation(world)).setBlockData(data);
        }
    }

    public int getHeight() {
        return getUpperX()-getLowerX() + 1;
    }

    public int getWidthX() {
        return getUpperY()-getLowerY() + 1;
    }

    public int getWidthZ() {
        return getUpperZ()-getLowerZ() + 1;
    }

    public boolean hasSameDimensions(BlockArea other) {
        return getHeight() == other.getHeight() && getWidthX() == other.getWidthX() && getWidthZ() == other.getWidthZ();
    }

    public boolean is2d() {
        return getHeight() == 1 || getWidthX() == 1 || getLowerZ() == 1;
    }

    public List<BlockPosition> pickRandomBlocks(int limit, Random random) {
        return CollectionUtil.pickRandomElements(getBlocks(), limit, random);
    }

    public BlockPosition pickRandomBlock(Random random) {
        return CollectionUtil.pickRandomElement(getBlocks(), random);
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
        BlockArea area = (BlockArea) o;
        return Objects.equals(position1, area.position1) && Objects.equals(position2, area.position2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position1, position2);
    }
}
