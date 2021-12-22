package io.github.lama06.llamagames;

import com.google.gson.*;
import io.github.lama06.llamagames.util.BlockDataTypeAdapter;
import io.github.lama06.llamagames.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class GameManager implements Listener {
    private static final Set<Pair<Class<?>, TypeAdapter<?>>> DEFAULT_TYPE_ADAPTERS = Set.of(
            Pair.of(BlockData.class, new BlockDataTypeAdapter())
    );

    private final LlamaGamesPlugin plugin;
    private final File configFile;
    private final Set<Game<?, ?>> games = new HashSet<>();

    public GameManager(LlamaGamesPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "games.json");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private Gson createGson(GameType<?, ?> type) {
        GsonBuilder builder = new GsonBuilder().serializeNulls().setPrettyPrinting();

        Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters = type.getTypeAdapters();
        if (typeAdapters != null) {
            for (Pair<Class<?>, TypeAdapter<?>> typeAdapter : typeAdapters) {
                builder.registerTypeAdapter(typeAdapter.getLeft(), typeAdapter.getRight());
            }
        }

        for (Pair<Class<?>, TypeAdapter<?>> typeAdapter : DEFAULT_TYPE_ADAPTERS) {
            builder.registerTypeAdapter(typeAdapter.getLeft(), typeAdapter.getRight());
        }

        return builder.create();
    }

    private JsonObject loadGamesConfig() throws GamesLoadFailedException {
        boolean created;

        try {
            created = configFile.createNewFile();
        } catch (IOException e) {
            throw new GamesLoadFailedException("Failed to create the config file", e);
        }

        if (created) {
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("{}");

                return new JsonObject();
            } catch (IOException e) {
                throw new GamesLoadFailedException("Failed to write to the config file", e);
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonElement gamesConfig = JsonParser.parseReader(reader);

            if (!gamesConfig.isJsonObject()) {
                throw new GamesLoadFailedException("The games config file does not contain a root object");
            }

            return gamesConfig.getAsJsonObject();
        } catch (IOException e) {
            throw new GamesLoadFailedException("Failed to load games config file", e);
        } catch (JsonParseException e) {
            throw new GamesLoadFailedException("Failed to parse games config file", e);
        }
    }

    private <G extends Game<G, C>, C extends GameConfig> void loadGame(GameType<G, C> type, World world, JsonObject config) {
        Gson gson = createGson(type);

        C deserializedConfig = gson.fromJson(config, type.getConfigType());

        G game = type.getCreator().createGame(plugin, world, deserializedConfig, type);
        games.add(game);

        game.loadGame();
    }

    public void loadGames() throws GamesLoadFailedException {
        JsonObject gamesConfig = loadGamesConfig();

        for (Map.Entry<String, JsonElement> gameEntry : gamesConfig.entrySet()) {
            String worldName = gameEntry.getKey();
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }

            if (!gameEntry.getValue().isJsonObject()) {
                throw new GamesLoadFailedException("The games config file has an invalid format");
            }

            if (!gameEntry.getValue().getAsJsonObject().has("type") ||
                    !gameEntry.getValue().getAsJsonObject().get("type").isJsonPrimitive() ||
                    !gameEntry.getValue().getAsJsonObject().get("type").getAsJsonPrimitive().isString()) {
                throw new GamesLoadFailedException("The games config file contains a game without a type attribute");
            }
            String gameTypeName = gameEntry.getValue().getAsJsonObject().get("type").getAsString();
            Optional<GameType<?, ?>> type = GameType.getByName(gameTypeName);
            if (type.isEmpty()) {
                throw new GamesLoadFailedException(String.format("Could not find game type: %s", gameTypeName));
            }

            if (!gameEntry.getValue().getAsJsonObject().has("config") || !gameEntry.getValue().getAsJsonObject().get("config").isJsonObject()) {
                throw new GamesLoadFailedException("The game config file contains a game without a config attribute");
            }
            JsonObject gameConfig = gameEntry.getValue().getAsJsonObject().get("config").getAsJsonObject();

            loadGame(type.get(), world, gameConfig);
        }
    }

    public void saveGameConfig() throws GamesSaveFailedException {
        JsonObject gamesConfig = new JsonObject();
        gamesConfig.addProperty("dataVersion", 1);

        for (Game<?, ?> game : games) {
            Gson gson = createGson(game.getType());

            JsonObject gameConfigEntry = new JsonObject();
            gameConfigEntry.addProperty("type", game.getType().getName());

            JsonObject gameConfig = gson.toJsonTree(game.getConfig()).getAsJsonObject();
            gameConfigEntry.add("config", gameConfig);

            gamesConfig.add(game.getWorld().getName(), gameConfigEntry);
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(gamesConfig, writer);
        } catch (IOException e) {
            throw new GamesSaveFailedException("Failed to write to games.json", e);
        }
    }

    public void unloadGames() {
        for (Game<?, ?> game : games) {
            game.unloadGame();
            games.remove(game);
        }
    }

    public Optional<Game<?, ?>> getGameForWorld(World world) {
        return games.stream().filter(game -> game.getWorld().equals(world)).findFirst();
    }

    public <G extends Game<G, C>, C extends GameConfig> void createGame(World world, GameType<G, C> type) {
        if (games.stream().anyMatch(game -> game.getWorld().equals(world))) {
            return;
        }

        C config = type.getDefaultConfigCreator().get();
        G game = type.getCreator().createGame(plugin, world, config, type);

        games.add(game);
        game.loadGame();
    }

    public boolean deleteGame(World world) {
        Optional<Game<?, ?>> game = games.stream().filter(g -> g.getWorld().equals(world)).findFirst();
        if (game.isPresent()) {
            games.remove(game.get());
            game.get().unloadGame();
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleWorldUnloadEvent(WorldUnloadEvent event) {
        if (games.stream().anyMatch(game -> game.getWorld().equals(event.getWorld()))) {
            event.setCancelled(true);
        }
    }

    public Set<Game<?, ?>> getGames() {
        return games;
    }

    public static class GamesLoadFailedException extends Exception {
        public GamesLoadFailedException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public GamesLoadFailedException(String msg) {
            super(msg);
        }
    }

    public static class GamesSaveFailedException extends Exception {
        public GamesSaveFailedException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
