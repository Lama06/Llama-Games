package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.GsonConstructor;

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
