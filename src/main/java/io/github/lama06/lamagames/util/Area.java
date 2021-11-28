package io.github.lama06.lamagames.util;

public class Area {
    private BlockPosition position1;
    private BlockPosition position2;

    public Area(BlockPosition position1, BlockPosition position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    // Gson
    public Area() {

    }

    public BlockPosition getPosition1() {
        return position1;
    }

    public BlockPosition getPosition2() {
        return position2;
    }
}
