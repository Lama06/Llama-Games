package io.github.lama06.lamagames.lama_says;

import com.google.gson.TypeAdapter;
import io.github.lama06.lamagames.Game;
import io.github.lama06.lamagames.GameType;
import io.github.lama06.lamagames.LamaGamesPlugin;
import io.github.lama06.lamagames.util.Pair;
import org.bukkit.World;

import java.util.Random;
import java.util.Set;

public class LamaSaysGame extends Game<LamaSaysGame, LamaSaysConfig> {
    private final Random random = new Random();

    public LamaSaysGame(LamaGamesPlugin plugin, World world, LamaSaysConfig config, GameType<LamaSaysGame, LamaSaysConfig> type) {
        super(plugin, world, config, type);
    }

    @Override
    public Set<Pair<Class<?>, TypeAdapter<?>>> getConfigTypeAdapters() {
        return null;
    }

    @Override
    public void handleGameStarted() {
        new LamaSaysCommand(plugin, "lamagames");
    }

    @Override
    public void handleGameEnded() {

    }

    @Override
    public boolean canStart() {
        return true;
    }

    public Random getRandom() {
        return random;
    }
}
