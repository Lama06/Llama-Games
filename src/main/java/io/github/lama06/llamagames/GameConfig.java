package io.github.lama06.llamagames;

import io.github.lama06.llamagames.util.EntityPosition;

public class GameConfig {
    private EntityPosition spawnPoint;
    private boolean cancelEvents = true;
    private boolean doNotCancelOpEvents = true;

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
