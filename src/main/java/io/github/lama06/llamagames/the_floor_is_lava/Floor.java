package io.github.lama06.llamagames.the_floor_is_lava;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.Named;

public class Floor implements Named {
    public String name;
    public BlockArea blocks;

    // Gson
    public Floor() { }

    public Floor(String name, BlockArea blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    @Override
    public String getName() {
        return name;
    }
}
