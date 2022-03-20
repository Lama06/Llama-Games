package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.GsonConstructor;

public class PowerSwitch {
    public int gold;
    public BlockPosition activationBlock;

    @GsonConstructor
    public PowerSwitch() { }

    public PowerSwitch(int gold, BlockPosition activationBlock) {
        this.gold = gold;
        this.activationBlock = activationBlock;
    }
}
