package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.GsonConstructor;
import io.github.lama06.llamagames.util.Named;

public class Floor implements Named {
    private String name;
    private BlockArea area;

    @GsonConstructor
    public Floor() { }

    public Floor(String name, BlockArea area) {
        this.name = name;
        this.area = area;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockArea getArea() {
        return area;
    }

    public void setArea(BlockArea area) {
        this.area = area;
    }
}
