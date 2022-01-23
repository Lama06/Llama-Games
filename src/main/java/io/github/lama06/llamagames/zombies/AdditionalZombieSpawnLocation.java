package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.util.EntityPosition;

import java.util.Objects;

public class AdditionalZombieSpawnLocation {
    public String name;
    public String area;
    public EntityPosition position;

    // Gson
    public AdditionalZombieSpawnLocation() { }

    public AdditionalZombieSpawnLocation(String name, String area, EntityPosition position) {
        this.name = name;
        this.area = area;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalZombieSpawnLocation that = (AdditionalZombieSpawnLocation) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
