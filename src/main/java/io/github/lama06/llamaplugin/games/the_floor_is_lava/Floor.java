package io.github.lama06.llamaplugin.games.the_floor_is_lava;

import io.github.lama06.llamaplugin.util.Named;
import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.GsonConstructor;

public class Floor implements Named {
    public String name;
    public BlockArea blocks;

    @GsonConstructor
    public Floor() { }

    public Floor(String name, BlockArea blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    @Override
    public String getName() {
        return name;
    }
}
