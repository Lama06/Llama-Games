package io.github.lama06.llamaplugin.games;

import io.github.lama06.llamaplugin.util.EntityPosition;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class GameConfig {
    private EntityPosition spawnPoint;
    private boolean cancelEvents = true;
    private boolean doNotCancelOpEvents = true;

    @MustBeInvokedByOverriders
    public boolean isComplete() {
        return getSpawnPoint() != null;
    }

    public EntityPosition getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(EntityPosition spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public boolean isCancelEvents() {
        return cancelEvents;
    }

    public void setCancelEvents(boolean cancelEvents) {
        this.cancelEvents = cancelEvents;
    }

    public boolean isDoNotCancelOpEvents() {
        return doNotCancelOpEvents;
    }

    public void setDoNotCancelOpEvents(boolean doNotCancelOpEvents) {
        this.doNotCancelOpEvents = doNotCancelOpEvents;
    }
}
