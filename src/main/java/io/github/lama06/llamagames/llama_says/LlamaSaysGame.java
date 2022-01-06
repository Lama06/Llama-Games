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
    private MiniGame currentMiniGame;
    private Map<UUID, Integer> points;

    public LlamaSaysGame(LlamaGamesPlugin plugin, World world, LlamaSaysConfig config, GameType<LlamaSaysGame, LlamaSaysConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public void handleGameStarted(String[] args) {
        MiniGameType<?> type = null;
        if (args != null && args.length == 1) {
            Optional<MiniGameType<?>> result = MiniGameType.byName(args[0]);
            if (result.isPresent()) {
                type = result.get();
            }
        }

        remainingRounds = config.getNumberOfRounds();
        remainingGameTypes = new ArrayList<>(MiniGameType.getTypes());

        points = new HashMap<>();
        for (Player player : getPlayers()) {
            points.put(player.getUniqueId(), 0);
        }

        startNextRound(type);
    }

    @Override
    public void handlePlayerLeft(Player player) {
        if (currentMiniGame != null) {
            currentMiniGame.handlePlayerLeft(player);
        }
    }

    private void startNextRound(MiniGameType<?> type) {
        remainingRounds--;

        if (type == null) {
            if (remainingGameTypes.isEmpty()) {
                type = MiniGameType.getTypes().get(random.nextInt(MiniGameType.getTypes().size()));
            } else {
                type = remainingGameTypes.get(random.nextInt(remainingGameTypes.size()));
                remainingGameTypes.remove(type);
            }
        }

        currentMiniGame = type.getCreator().createMiniGame(this, game -> {
            canceler.disallowAll();

            MiniGameResult result = game.getResult();

            for (Player player : getPlayers()) {
                int currentPoints = points.get(player.getUniqueId());
                int newPoints = currentPoints + result.getPointsForPlayer(player);
                points.put(player.getUniqueId(), newPoints);
            }

            if (remainingRounds == 0) {
                endGame(GameEndReason.ENDED);
            } else {
                startNextRound(null);
            }
        });

        currentMiniGame.startGame();
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        if (currentMiniGame != null) {
            currentMiniGame.endGame(false);
            currentMiniGame = null;
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

        if (config.getFloor() == null || config.getFloor().getPosition1() == null || config.getFloor().getPosition2() == null) {
            result = false;
        }

        if (config.getFloorCenter() == null || config.getFloorMaterial() == null) {
            result = false;
        }

        return result;
    }
}
