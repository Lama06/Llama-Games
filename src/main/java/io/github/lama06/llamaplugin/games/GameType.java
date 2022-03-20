package io.github.lama06.llamaplugin.games;

import com.google.gson.TypeAdapter;
import io.github.lama06.llamaplugin.games.blockparty.BlockPartyCommand;
import io.github.lama06.llamaplugin.games.blockparty.BlockPartyConfig;
import io.github.lama06.llamaplugin.games.blockparty.BlockPartyGame;
import io.github.lama06.llamaplugin.games.llama_says.LlamaSaysCommand;
import io.github.lama06.llamaplugin.games.llama_says.LlamaSaysConfig;
import io.github.lama06.llamaplugin.games.llama_says.LlamaSaysGame;
import io.github.lama06.llamaplugin.games.the_floor_is_lava.TheFloorIsLavaCommand;
import io.github.lama06.llamaplugin.games.the_floor_is_lava.TheFloorIsLavaConfig;
import io.github.lama06.llamaplugin.games.the_floor_is_lava.TheFloorIsLavaGame;
import io.github.lama06.llamaplugin.games.zombies.ZombiesCommand;
import io.github.lama06.llamaplugin.games.zombies.ZombiesConfig;
import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.games.zombies.monster.MonsterType;
import io.github.lama06.llamaplugin.games.zombies.monster.MonsterTypeAdapter;
import io.github.lama06.llamaplugin.games.zombies.weapon.WeaponType;
import io.github.lama06.llamaplugin.games.zombies.weapon.WeaponTypeAdapter;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Map;
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
            "llama_says",
            LlamaSaysGame::new,
            LlamaSaysConfig.class,
            null,
            LlamaSaysConfig::new,
            LlamaSaysCommand::new,
            null
    );

    public static final GameType<BlockPartyGame, BlockPartyConfig> BLOCK_PARTY = new GameType<>(
            "block_party",
            BlockPartyGame::new,
            BlockPartyConfig.class,
            null,
            BlockPartyConfig::new,
            BlockPartyCommand::new,
            null
    );

    public static final GameType<ZombiesGame, ZombiesConfig> ZOMBIES = new GameType<>(
            "zombies",
            ZombiesGame::new,
            ZombiesConfig.class,
            Map.ofEntries(
                    Map.entry(MonsterType.class, new MonsterTypeAdapter()),
                    Map.entry(WeaponType.class, new WeaponTypeAdapter())
            ),
            ZombiesConfig::new,
            ZombiesCommand::new,
            null
    );

    public static final GameType<TheFloorIsLavaGame, TheFloorIsLavaConfig> THE_FLOOR_IS_LAVA = new GameType<>(
            "the_floor_is_lava",
            TheFloorIsLavaGame::new,
            TheFloorIsLavaConfig.class,
            null, TheFloorIsLavaConfig::new,
            TheFloorIsLavaCommand::new,
            null
    );

    private final String name;
    private final GameCreator<G, C> creator;
    private final Class<C> configType;
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
    private final Supplier<C> defaultConfigCreator;
    private final Consumer<GamesModule> pluginEnableCallback;
    private final Consumer<GamesModule> pluginDisableCallback;

    private GameType(
            String name,
            GameCreator<G, C> creator,
            Class<C> configType,
            Map<Class<?>, TypeAdapter<?>> typeAdapters,
            Supplier<C> defaultConfigCreator,
            Consumer<GamesModule> pluginEnableCallback,
            Consumer<GamesModule> pluginDisableCallback
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

    public Map<Class<?>, TypeAdapter<?>> getTypeAdapters() {
        return typeAdapters;
    }

    public Supplier<C> getDefaultConfigCreator() {
        return defaultConfigCreator;
    }

    public Consumer<GamesModule> getPluginEnableCallback() {
        return pluginEnableCallback;
    }

    public Consumer<GamesModule> getPluginDisableCallback() {
        return pluginDisableCallback;
    }

    @FunctionalInterface
    public interface GameCreator<G extends Game<G, C>, C extends GameConfig> {
        G createGame(GamesModule module, World world, C config, GameType<G, C> type);
    }
}
