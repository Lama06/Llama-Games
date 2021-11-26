package io.github.lama06.lamagames.lama_says;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract non-sealed class CompleteMiniGame<T extends CompleteMiniGame<T>> extends MiniGame<T> {
    private final Set<UUID> successfulPlayers = new HashSet<>();

    public CompleteMiniGame(LamaSaysGame game, Consumer<T> callback) {
        super(game, callback);
    }

    protected void addSuccessfulPlayer(Player player) {
        successfulPlayers.add(player.getUniqueId());
    }

    public Set<UUID> getSuccessfulPlayers() {
        return successfulPlayers;
    }
}
