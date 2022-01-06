package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import org.bukkit.Material;

public class LlamaSaysConfig extends GameConfig {
    private int numberOfRounds = 10;
    private BlockArea floor = null;
    private BlockPosition floorCenter = null;
    private Material floorMaterial;

    // Gson
    public LlamaSaysConfig() { }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public BlockArea getFloor() {
        return floor;
    }

    public void setFloor(BlockArea floor) {
        this.floor = floor;
    }

    public BlockPosition getFloorCenter() {
        return floorCenter;
    }

    public void setFloorCenter(BlockPosition floorCenter) {
        this.floorCenter = floorCenter;
    }

    public Material getFloorMaterial() {
        return floorMaterial;
    }

    public void setFloorMaterial(Material floorMaterial) {
        this.floorMaterial = floorMaterial;
    }
}
