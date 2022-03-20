package io.github.lama06.llamaplugin.games.blockparty;

import com.google.common.collect.ImmutableMap;
import io.github.lama06.llamaplugin.games.GameConfig;
import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.GsonConstructor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlockPartyConfig extends GameConfig {
    public static final Map<Integer, Integer> DEFAULT_ROUND_TIMES = ImmutableMap.<Integer, Integer>builder()
            .put(1, 150)
            .put(2, 120)
            .put(3, 100)
            .put(4, 90)
            .put(5, 80)
            .put(6, 70)
            .put(7, 60)
            .put(8, 50)
            .put(9, 45)
            .put(-1, 40)
            .build();

    private BlockArea floor;
    private Set<Floor> floors = new HashSet<>();
    private Map<Integer, Integer> roundTimes = DEFAULT_ROUND_TIMES;
    private Material deadlyBlock;

    @GsonConstructor
    public BlockPartyConfig() { }

    @Override
    public boolean isComplete() {
            return super.isComplete() && deadlyBlock != null && floor != null && !floors.isEmpty() && roundTimes.containsKey(-1);
    }

    public Optional<Floor> getFloorByName(String name) {
        return getFloors().stream().filter(floor -> floor.getName().equals(name)).findFirst();
    }

    public BlockArea getFloor() {
        return floor;
    }

    public void setFloor(BlockArea floor) {
        this.floor = floor;
    }

    public Set<Floor> getFloors() {
        return floors;
    }

    public void setFloors(Set<Floor> floors) {
        this.floors = floors;
    }

    public Map<Integer, Integer> getRoundTimes() {
        return roundTimes;
    }

    public void setRoundTimes(Map<Integer, Integer> roundTimes) {
        this.roundTimes = roundTimes;
    }

    public Material getDeadlyBlock() {
        return deadlyBlock;
    }

    public void setDeadlyBlock(Material deadlyBlock) {
        this.deadlyBlock = deadlyBlock;
    }
}
