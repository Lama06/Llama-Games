package io.github.lama06.lamagames;

import com.google.gson.TypeAdapter;
import io.github.lama06.lamagames.util.Pair;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    private final String name;
    private final GameCreator<G, C> creator;
    private final Class<C> configType;
    private final Supplier<C> defaultConfigCreator;
    private final Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters;

    private GameType(String name, GameCreator<G, C> creator, Class<C> configType, Supplier<C> defaultConfigCreator, Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters) {
        this.name = name;
        this.creator = creator;
        this.configType = configType;
        this.defaultConfigCreator = defaultConfigCreator;
        this.typeAdapters = typeAdapters;

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

    public Set<Pair<Class<?>, TypeAdapter<?>>> getTypeAdapters() {
        return typeAdapters;
    }

    @FunctionalInterface
    public interface GameCreator<G extends Game<G, C>, C> {
        G createGame(LamaGamesPlugin plugin, World world, C config);
    }
}
