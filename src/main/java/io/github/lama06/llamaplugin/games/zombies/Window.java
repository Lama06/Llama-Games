package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.util.Named;
import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.EntityPosition;
import io.github.lama06.llamaplugin.util.GsonConstructor;

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
