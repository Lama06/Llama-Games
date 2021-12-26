package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.Game;
import io.github.lama06.llamagames.GameType;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

public class LlamaSaysGame extends Game<LlamaSaysGame, LlamaSaysConfig> {
    private int remainingRounds;
    private List<MiniGameType<?>> remainingGameTypes;
    private MiniGame<?> currentMiniGame;
    private Map<UUID, Integer> points;

    public LlamaSaysGame(LlamaGamesPlugin plugin, World world, LlamaSaysConfig config, GameType<LlamaSaysGame, LlamaSaysConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted() {
        remainingRounds = config.numberOfRounds;
        remainingGameTypes = new ArrayList<>(MiniGameType.getTypes());
        points = new HashMap<>();
        for (Player player : getPlayers()) {
            points.put(player.getUniqueId(), 0);
        }
        startNextRound();
    }

    private void startNextRound() {
        remainingRounds--;

        MiniGameType<?> type;
        if (remainingGameTypes.isEmpty()) {
            type = MiniGameType.getTypes().get(random.nextInt(MiniGameType.getTypes().size()));
        } else {
            type = remainingGameTypes.get(random.nextInt(remainingGameTypes.size()));
            remainingGameTypes.remove(type);
        }

        currentMiniGame = type.getCreator().createMiniGame(this, game -> {
            canceler.disallowAll();

            if (game instanceof CompeteMiniGame<?> compete) {
                List<UUID> ranking = compete.getRanking();

                if (ranking.size() >= 1) points.put(ranking.get(0), points.get(ranking.get(0)) + 3);
                if (ranking.size() >= 2) points.put(ranking.get(1), points.get(ranking.get(1)) + 2);
                if (ranking.size() >= 3) points.put(ranking.get(2), points.get(ranking.get(2)) + 1);
            } else if (game instanceof CompleteMiniGame<?> complete) {
                Set<UUID> successfulPlayers = complete.getSuccessfulPlayers();

                for (UUID successfulPlayer : successfulPlayers) {
                    points.put(successfulPlayer, points.get(successfulPlayer) + 1);
                }
            }

            if (remainingRounds == 0) {
                endGame(GameEndReason.ENDED);
            } else {
                startNextRound();
            }
        });

        currentMiniGame.startGame();
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        if (currentMiniGame != null) {
            currentMiniGame.endGame(false);
        }

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Results: ").color(NamedTextColor.GOLD)).append(Component.newline());

        int i = 1;
        for (Player player : getPlayers()) {
            builder.append(Component.text("%d. %s: %d".formatted(i, player.getName(), points.get(player.getUniqueId()))).color(i <= 3 ? NamedTextColor.GOLD : NamedTextColor.BLUE));
            i++;
        }

        getBroadcastAudience().sendMessage(builder);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && world.getPlayers().size() >= 1;
    }

    @Override
    public boolean canContinueAfterPlayerLeft() {
        return getPlayers().size() >= 1;
    }

    @Override
    public boolean isConfigComplete() {
        boolean result = super.isConfigComplete();

        if (config.floor == null || config.floor.getPosition1() == null || config.floor.getPosition2() == null) {
            result = false;
        }

        if (config.floorCenter == null) {
            result = false;
        }

        return result;
    }
}
