package io.github.lama06.llamaplugin.games.zombies;

import io.github.lama06.llamaplugin.util.Named;
import io.github.lama06.llamaplugin.util.EntityPosition;
import io.github.lama06.llamaplugin.util.GsonConstructor;

public class AdditionalZombieSpawnLocation implements Named {
    public String name;
    public String area;
    public EntityPosition position;

    @GsonConstructor
    public AdditionalZombieSpawnLocation() { }

    public AdditionalZombieSpawnLocation(String name, String area, EntityPosition position) {
        this.name = name;
        this.area = area;
        this.position = position;
    }

    @Override
    public String getName() {
        return name;
    }
}
