package io.github.lama06.llamagames.blockparty;

import com.google.common.collect.ImmutableMap;
import io.github.lama06.llamagames.GameConfig;
import io.github.lama06.llamagames.util.BlockArea;
import org.bukkit.Material;

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

    public BlockArea floor;
    public Set<Floor> floors;
    public Map<Integer, Integer> roundTimes = DEFAULT_ROUND_TIMES;
    public Material deadlyBlock;

    // Gson
    public BlockPartyConfig() { }

    public Optional<Floor> getFloorByName(String name) {
        return floors.stream().filter(floor -> floor.name.equals(name)).findFirst();
    }
}
