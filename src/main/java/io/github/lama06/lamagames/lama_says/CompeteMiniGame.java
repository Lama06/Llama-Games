package io.github.lama06.lamagames.lama_says;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract non-sealed class CompeteMiniGame<T extends CompeteMiniGame<T>> extends MiniGame<T> {
    private final List<UUID> ranking = new ArrayList<>();

    public CompeteMiniGame(LamaSaysGame game, Consumer<T> callback) {
        super(game, callback);
    }

    protected final void addSuccessfulPlayer(Player player) {
        if (!ranking.contains(player.getUniqueId())) {
            ranking.add(player.getUniqueId());
        }
    }

    public final List<UUID> getRanking() {
        return ranking;
    }
}
