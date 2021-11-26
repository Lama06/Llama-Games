package io.github.lama06.lamagames;

import io.github.lama06.lamagames.lama_says.LamaSaysConfig;
import io.github.lama06.lamagames.lama_says.LamaSaysGame;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class GameType<G extends Game<G, C>, C> {
    private final static Set<GameType<?, ?>> values = new HashSet<>();

    public static Set<GameType<?, ?>> getValues() {
        return values;
    }

    public static Optional<GameType<?, ?>> getByName(String name) {
        return values.stream().filter(type -> type.name.equalsIgnoreCase(name)).findFirst();
    }

    public static final GameType<LamaSaysGame, LamaSaysConfig> LAMA_SAYS = new GameType<>(
            "lama_says",
            LamaSaysGame::new,
            LamaSaysConfig.class,
            LamaSaysConfig::new,
            LamaSaysGame::onPluginEnabled,
            null
    );

    private final String name;
    private final GameCreator<G, C> creator;
    private final Class<C> configType;
    private final Supplier<C> defaultConfigCreator;
    private final Consumer<LamaGamesPlugin> pluginEnableCallback;
    private final Consumer<LamaGamesPlugin> pluginDisableCallback;

    private GameType(
            String name,
            GameCreator<G, C> creator,
            Class<C> configType,
            Supplier<C> defaultConfigCreator,
            Consumer<LamaGamesPlugin> pluginEnableCallback,
            Consumer<LamaGamesPlugin> pluginDisableCallback
    ) {
        this.name = name;
        this.creator = creator;
        this.configType = configType;
        this.defaultConfigCreator = defaultConfigCreator;
        this.pluginEnableCallback = pluginEnableCallback;
        this.pluginDisableCallback = pluginDisableCallback;

        values.add(this);
    }

    public String getName() {
        return name;
    }

    public GameCreator<G, C> getCreator() {
        return creator;
    }

    public Class<C> getConfigType() {
        return configType;
    }

    public Supplier<C> getDefaultConfigCreator() {
        return defaultConfigCreator;
    }

    public Consumer<LamaGamesPlugin> getPluginEnableCallback() {
        return pluginEnableCallback;
    }

    public Consumer<LamaGamesPlugin> getPluginDisableCallback() {
        return pluginDisableCallback;
    }

    @FunctionalInterface
    public interface GameCreator<G extends Game<G, C>, C> {
        G createGame(LamaGamesPlugin plugin, World world, GameType<G, C> type);
    }
}
