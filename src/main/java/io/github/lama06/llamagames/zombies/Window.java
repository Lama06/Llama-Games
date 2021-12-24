package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.EntityPosition;

public class Window {
    public BlockArea windowBlocks;
    public EntityPosition zombieSpawnLocation;
    public String area;
    
    // Gson
    public Window() { }

    public Window(BlockArea windowBlocks, EntityPosition zombieSpawnLocation, String area) {
        this.windowBlocks = windowBlocks;
        this.zombieSpawnLocation = zombieSpawnLocation;
        this.area = area;
    }
}
