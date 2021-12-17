package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract non-sealed class CompleteMiniGame<T extends CompleteMiniGame<T>> extends MiniGame<T> {
    private final Set<UUID> successfulPlayers = new HashSet<>();
    private final Set<UUID> failedPlayers = new HashSet<>();

    public CompleteMiniGame(LlamaSaysGame game, Consumer<T> callback) {
        super(game, callback);
    }

    protected void addSuccessfulPlayer(Player player) {
        if (!failedPlayers.contains(player.getUniqueId()) && !successfulPlayers.contains(player.getUniqueId())) {
            successfulPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("Success").color(NamedTextColor.GREEN));
        }
    }

    protected void addFailedPlayer(Player player) {
        if (!failedPlayers.contains(player.getUniqueId()) && !successfulPlayers.contains(player.getUniqueId())) {
            failedPlayers.add(player.getUniqueId());
            player.sendMessage(Component.text("You failed").color(NamedTextColor.RED));
        }
    }

    public Set<UUID> getSuccessfulPlayers() {
        return successfulPlayers;
    }

    public Set<UUID> getFailedPlayers() {
        return failedPlayers;
    }
}
