package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.util.GsonConstructor;
import io.github.lama06.llamagames.util.Named;

public class Window implements Named {
    public String name;
    public String area;
    public EntityPosition spawnLocation;
    public BlockArea blocks;

    @GsonConstructor
    public Window() { }

    public Window(String name, String area, EntityPosition spawnLocation, BlockArea blocks) {
        this.name = name;
        this.area = area;
        this.spawnLocation = spawnLocation;
        this.blocks = blocks;
    }

    @Override
    public String getName() {
        return name;
    }
}
