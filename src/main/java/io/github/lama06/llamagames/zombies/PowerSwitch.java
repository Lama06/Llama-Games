package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockPosition;

public class PowerSwitch {
    public BlockPosition position;
    public int price;

    // Gson
    public PowerSwitch() { }

    public PowerSwitch(BlockPosition position, int price) {
        this.position = position;
        this.price = price;
    }
}
