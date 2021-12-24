package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;

public class LlamaSaysConfig extends GameConfig {
    public int numberOfRounds = 10;
    public BlockArea floor = null;
    public BlockPosition floorCenter = null;

    // Gson
    public LlamaSaysConfig() { }
}
