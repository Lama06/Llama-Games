package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.EntityPosition;

import java.util.Objects;

public class Window {
    public String name;
    public String area;
    public BlockArea windowBlocks;
    public EntityPosition zombieSpawnLocation;

    // Gson
    public Window() { }

    public Window(String name, String area, BlockArea windowBlocks, EntityPosition zombieSpawnLocation) {
        this.name = name;
        this.area = area;
        this.windowBlocks = windowBlocks;
        this.zombieSpawnLocation = zombieSpawnLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window window = (Window) o;
        return Objects.equals(name, window.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
