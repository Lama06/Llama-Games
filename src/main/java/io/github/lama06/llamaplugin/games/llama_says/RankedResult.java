package io.github.lama06.llamaplugin.games.llama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;

public final class RankedResult implements MiniGameResult {
    private final LlamaSaysGame game;
    private final List<UUID> ranking = new ArrayList<>();
    private final Set<UUID> failedPlayers = new HashSet<>();

    public RankedResult(LlamaSaysGame game) {
        this.game = game;
    }

    @Override
    public void addSuccessfulPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failedPlayers.contains(player.getUniqueId())) {
            ranking.add(player.getUniqueId());
            player.sendMessage(Component.text("You got %d".formatted(ranking.size())).color(NamedTextColor.GREEN));
        }
    }

    @Override
    public void handleGameEnded() {
        game.getPlayers().forEach(this::addFailedPlayer);
    }

    @Override
    public void addFailedPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failedPlayers.contains(player.getUniqueId())) {
            failedPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("You failed").color(NamedTextColor.RED));
        }
    }

    @Override
    public int getPointsForPlayer(Player player) {
        int position = ranking.indexOf(player.getUniqueId());

        return switch (position) {
            case 0 -> 3;
            case 1 -> 2;
            case 2 -> 1;
            default -> 0;
        };
    }

    @Override
    public List<UUID> getSuccessfulPlayers() {
        return ranking;
    }

    @Override
    public Set<UUID> getFailedPlayers() {
        return failedPlayers;
    }
}
