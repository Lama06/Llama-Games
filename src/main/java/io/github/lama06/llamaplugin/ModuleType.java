package io.github.lama06.llamaplugin;

import io.github.lama06.llamaplugin.games.GamesModule;
import io.github.lama06.llamaplugin.util.Named;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record ModuleType<T extends Module<T>>(String name, ModuleCreator<T> creator) implements Named {
    private static final Set<ModuleType<?>> VALUES = new HashSet<>();

    public static Set<ModuleType<?>> getValues() {
        return VALUES;
    }

    public static Optional<ModuleType<?>> byName(String name) {
        return VALUES.stream().filter(type -> type.name.equals(name)).findFirst();
    }

    public static final ModuleType<GamesModule> GAMES = new ModuleType<>("games", GamesModule::new);

    public ModuleType {
        VALUES.add(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @FunctionalInterface
    public interface ModuleCreator<T extends Module<T>> {
        T createModule(LlamaPlugin plugin, ModuleType<T> type);
    }
}
