package io.github.lama06.llamagames.llama_says;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public sealed interface MiniGameResult permits CompleteResult, RankedResult {
    void addFailedPlayer(Player player);

    void addSuccessfulPlayer(Player player);

    void handleGameEnded();

    int getPointsForPlayer(Player player);

    Collection<UUID> getSuccessfulPlayers();

    Collection<UUID> getFailedPlayers();
}
