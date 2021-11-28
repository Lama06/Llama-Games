package io.github.lama06.lamagames.lama_says;

import io.github.lama06.lamagames.Game;
import io.github.lama06.lamagames.GameType;
import io.github.lama06.lamagames.LamaGamesPlugin;
import io.github.lama06.lamagames.util.EventCanceler;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class LamaSaysGame extends Game<LamaSaysGame, LamaSaysConfig> {
    public static void onPluginEnabled(LamaGamesPlugin plugin) {
        new LamaSaysCommand(plugin, "lamasays");
    }

    private final Random random = new Random();
    private int remainingRounds;
    private List<MiniGameType<?>> remainingGameTypes;
    private MiniGame<?> currentMiniGame;
    private Map<UUID, Integer> points;
    private final EventCanceler canceler;

    public LamaSaysGame(LamaGamesPlugin plugin, World world, GameType<LamaSaysGame, LamaSaysConfig> type) {
        super(plugin, world, type);

        canceler = new EventCanceler(plugin, world);
        canceler.disallowAll();
    }

    @Override
    public void handleGameLoaded() {
        canceler.registerEvents();
    }

    @Override
    public void handleGameUnloaded() {
        canceler.unregisterEvents();
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

                if (ranking.size() == 1) points.put(ranking.get(0), points.get(ranking.get(0)) + 3);
                if (ranking.size() == 2) points.put(ranking.get(1), points.get(ranking.get(1)) + 2);
                if (ranking.size() == 3) points.put(ranking.get(2), points.get(ranking.get(2)) + 1);
            } else if (game instanceof CompleteMiniGame<?> complete) {
                Set<UUID> successfulPlayers = complete.getSuccessfulPlayers();
                for (UUID successfulPlayer : successfulPlayers) {
                    points.put(successfulPlayer, points.get(successfulPlayer) + 1);
                }
            }

            if (remainingRounds == 0) {
                endGame();
            } else {
                startNextRound();
            }
        });

        currentMiniGame.startGame();
    }

    @Override
    public void handleGameEnded() {
        if (currentMiniGame != null) {
            currentMiniGame.endGame(false);
        }
    }

    @Override
    public boolean canStart() {
        return world.getPlayers().size() >= 1;
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

    public Random getRandom() {
        return random;
    }

    public EventCanceler getEventCanceler() {
        return canceler;
    }
}
