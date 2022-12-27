package io.github.lama06.llamagames.the_floor_is_lava;

import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.util.GsonConstructor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TheFloorIsLavaConfig extends GameConfig {
    public Set<Floor> floors = new HashSet<>();
    public List<BlockData> blockStates = new ArrayList<>();
    public int blockAgeTime = 20;
    public Material deadlyBlock;

    @GsonConstructor
    public TheFloorIsLavaConfig() { }

    @Override
    public boolean isComplete() {
        return super.isComplete() && deadlyBlock != null;
    }
}
