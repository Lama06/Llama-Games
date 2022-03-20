package io.github.lama06.llamaplugin.games.llama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CompleteResult implements MiniGameResult {
    private final LlamaSaysGame game;
    private final Set<UUID> successfulPlayers = new HashSet<>();
    private final Set<UUID> failedPlayers = new HashSet<>();

    public CompleteResult(LlamaSaysGame game) {
        this.game = game;
    }

    @Override
    public void addFailedPlayer(Player player) {
        if (!failedPlayers.contains(player.getUniqueId()) && !successfulPlayers.contains(player.getUniqueId())) {
            failedPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("You failed").color(NamedTextColor.RED));
        }
    }

    @Override
    public void addSuccessfulPlayer(Player player) {
        if (!failedPlayers.contains(player.getUniqueId()) && !successfulPlayers.contains(player.getUniqueId())) {
            successfulPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("Success").color(NamedTextColor.GREEN));
        }
    }

    @Override
    public void handleGameEnded() {
        game.getPlayers().forEach(this::addFailedPlayer);
    }

    @Override
    public int getPointsForPlayer(Player player) {
        return successfulPlayers.contains(player.getUniqueId()) ? 1 : 0;
    }

    @Override
    public Set<UUID> getSuccessfulPlayers() {
        return successfulPlayers;
    }

    @Override
    public Set<UUID> getFailedPlayers() {
        return failedPlayers;
    }
}
