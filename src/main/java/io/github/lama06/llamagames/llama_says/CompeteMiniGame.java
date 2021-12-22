package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public abstract non-sealed class CompeteMiniGame<T extends CompeteMiniGame<T>> extends MiniGame<T> {
    private final List<UUID> ranking = new ArrayList<>();
    private final Set<UUID> failedPlayers = new HashSet<>();

    public CompeteMiniGame(LlamaSaysGame game, Consumer<T> callback) {
        super(game, callback);
    }

    protected void addSuccessfulPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failedPlayers.contains(player.getUniqueId())) {
            ranking.add(player.getUniqueId());
            player.sendMessage(Component.text("You got %d".formatted(ranking.size())).color(NamedTextColor.GREEN));
        }
    }

    protected void addFailedPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failedPlayers.contains(player.getUniqueId())) {
            failedPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("You failed").color(NamedTextColor.RED));
        }
    }

    public List<UUID> getRanking() {
        return ranking;
    }

    public Set<UUID> getFailedPlayers() {
        return failedPlayers;
    }
}
