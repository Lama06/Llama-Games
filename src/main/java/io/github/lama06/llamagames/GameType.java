package io.github.lama06.llamagames;

import com.google.gson.TypeAdapter;
import io.github.lama06.llamagames.llama_says.LlamaSaysCommand;
import io.github.lama06.llamagames.llama_says.LlamaSaysConfig;
import io.github.lama06.llamagames.llama_says.LlamaSaysGame;
import io.github.lama06.llamagames.util.Pair;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class GameType<G extends Game<G, C>, C extends GameConfig> {
    private final static Set<GameType<?, ?>> values = new HashSet<>();

    public static Set<GameType<?, ?>> getValues() {
        return values;
    }

    public static Optional<GameType<?, ?>> getByName(String name) {
        return values.stream().filter(type -> type.name.equalsIgnoreCase(name)).findFirst();
    }

    public static final GameType<LlamaSaysGame, LlamaSaysConfig> LAMA_SAYS = new GameType<>(
            "lama_says",
            LlamaSaysGame::new,
            LlamaSaysConfig.class,
            null,
            LlamaSaysConfig::new,
            plugin -> new LlamaSaysCommand(plugin, "lamasays"),
            null
    );

    private final String name;
    private final GameCreator<G, C> creator;
    private final Class<C> configType;
    private final Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters;
    private final Supplier<C> defaultConfigCreator;
    private final Consumer<LlamaGamesPlugin> pluginEnableCallback;
    private final Consumer<LlamaGamesPlugin> pluginDisableCallback;

    private GameType(
            String name,
            GameCreator<G, C> creator,
            Class<C> configType,
            Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters,
            Supplier<C> defaultConfigCreator,
            Consumer<LlamaGamesPlugin> pluginEnableCallback,
            Consumer<LlamaGamesPlugin> pluginDisableCallback
    ) {
        this.name = name;
        this.creator = creator;
        this.configType = configType;
        this.typeAdapters = typeAdapters;
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

    public Set<Pair<Class<?>, TypeAdapter<?>>> getTypeAdapters() {
        return typeAdapters;
    }

    public Supplier<C> getDefaultConfigCreator() {
        return defaultConfigCreator;
    }

    public Consumer<LlamaGamesPlugin> getPluginEnableCallback() {
        return pluginEnableCallback;
    }

    public Consumer<LlamaGamesPlugin> getPluginDisableCallback() {
        return pluginDisableCallback;
    }

    @FunctionalInterface
    public interface GameCreator<G extends Game<G, C>, C extends GameConfig> {
        G createGame(LlamaGamesPlugin plugin, World world, C config, GameType<G, C> type);
    }
}
