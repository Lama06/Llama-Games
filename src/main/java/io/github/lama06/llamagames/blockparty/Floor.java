package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.util.BlockArea;

public class Floor {
    public String name;
    public BlockArea area;

    // Gson
    public Floor() { }

    public Floor(String name, BlockArea area) {
        this.name = name;
        this.area = area;
    }
}
