package io.github.lama06.lamagames.lama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public abstract non-sealed class CompeteMiniGame<T extends CompeteMiniGame<T>> extends MiniGame<T> {
    private final List<UUID> ranking = new ArrayList<>();
    private final Set<UUID> failed = new HashSet<>();

    public CompeteMiniGame(LamaSaysGame game, Consumer<T> callback) {
        super(game, callback);
    }

    protected void addSuccessfulPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failed.contains(player.getUniqueId())) {
            ranking.add(player.getUniqueId());
            player.sendMessage(Component.text("You got %d".formatted(ranking.size())).color(NamedTextColor.GREEN));
        }
    }

    protected void addFailedPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId()) && !failed.contains(player.getUniqueId())) {
            failed.add(player.getUniqueId());
            player.sendMessage(Component.text("You failed").color(NamedTextColor.RED));
        }
    }

    public final List<UUID> getRanking() {
        return ranking;
    }
}
