package io.github.lama06.lamagames.lama_says;

import io.github.lama06.lamagames.GameConfig;
import io.github.lama06.lamagames.util.Area;
import io.github.lama06.lamagames.util.BlockPosition;

public class LamaSaysConfig extends GameConfig {
    public int numberOfRounds = 10;
    public Area floor = null;
    public BlockPosition floorCenter = null;

    // Gson
    public LamaSaysConfig() { }
}
