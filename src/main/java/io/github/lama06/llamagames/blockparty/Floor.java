package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.util.BlockArea;

import java.util.Objects;

public class Floor {
    private String name;
    private BlockArea area;

    // Gson
    public Floor() { }

    public Floor(String name, BlockArea area) {
        this.setName(name);
        this.setArea(area);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return Objects.equals(getName(), floor.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

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
