package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.util.BlockArea;

import java.util.Objects;

public class Floor {
    public String name;
    public BlockArea area;

    // Gson
    public Floor() { }

    public Floor(String name, BlockArea area) {
        this.name = name;
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return Objects.equals(name, floor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
