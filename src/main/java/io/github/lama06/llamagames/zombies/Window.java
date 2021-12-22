package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.Area;
import io.github.lama06.llamagames.util.BlockPosition;

public class Window {
    public Area windowBlocks;
    public BlockPosition zombieSpawnLocation;
    public String area;
    
    // Gson
    public Window() { }

    public Window(Area windowBlocks, BlockPosition zombieSpawnLocation, String area) {
        this.windowBlocks = windowBlocks;
        this.zombieSpawnLocation = zombieSpawnLocation;
        this.area = area;
    }
}
