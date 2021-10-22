package io.github.lama06.lamagames;

import com.google.gson.*;
import io.github.lama06.lamagames.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class GameManager implements Listener {
    private final JsonParser parser = new JsonParser();
    private final LamaGamesPlugin plugin;
    private final File configFile;
    private final Set<Game<?, ?>> games = new HashSet<>();


    public GameManager(LamaGamesPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "games.json");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private JsonObject getGamesConfig() throws GamesLoadFailedException {
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            throw new GamesLoadFailedException("Failed to create games config file", e);
        }

        FileReader reader;
        try {
            reader = new FileReader(configFile);
        } catch (IOException e) {
            throw new GamesLoadFailedException("Failed to load games config file", e);
        }

        JsonElement gamesConfig;
        try {
            gamesConfig = parser.parse(reader);
        } catch (JsonParseException e) {
            throw new GamesLoadFailedException("Failed to parse games config file", e);
        }

        if (!gamesConfig.isJsonObject()) {
            throw new GamesLoadFailedException("The games config file does not contain a root object");
        }
        return gamesConfig.getAsJsonObject();
    }

    private <G extends Game<G, C>, C> void loadGame(GameType<G, C> type, World world, JsonObject config) {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters = type.getTypeAdapters();
        if (typeAdapters != null) for (Pair<Class<?>, TypeAdapter<?>> typeAdapter : typeAdapters) builder.registerTypeAdapter(typeAdapter.getLeft(), typeAdapter.getRight());
        Gson gson = builder.create();

        C deserializedConfig = gson.fromJson(config, type.getConfigType());
        G game = type.getCreator().createGame(plugin, world, deserializedConfig);
        games.add(game);
        game.handleGameLoaded();
    }

    public void loadGames() throws GamesLoadFailedException {
        JsonObject gamesConfig = getGamesConfig();

        for (Map.Entry<String, JsonElement> gameEntry : gamesConfig.entrySet()) {
            String worldName = gameEntry.getKey();
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }

            if (!gameEntry.getValue().isJsonObject()) {
                throw new GamesLoadFailedException("The games config file has an invalid format");
            }

            if (!gameEntry.getValue().getAsJsonObject().has("type")
                    || !gameEntry.getValue().getAsJsonObject().get("type").isJsonPrimitive()
                    || !gameEntry.getValue().getAsJsonObject().get("type").getAsJsonPrimitive().isString()) {
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

    public void saveGames() throws GamesSaveFailedException {
        JsonObject gamesConfig = new JsonObject();
        gamesConfig.addProperty("dataVersion", 1);

        for (Game<?, ?> game : games) {
            game.handleGameUnloaded();

            GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
            Set<Pair<Class<?>, TypeAdapter<?>>> typeAdapters = game.getType().getTypeAdapters();
            if (typeAdapters != null) for (Pair<Class<?>, TypeAdapter<?>> typeAdapter : typeAdapters) builder.registerTypeAdapter(typeAdapter.getLeft(), typeAdapter.getRight());
            Gson gson = builder.create();

            JsonObject gameConfigEntry = new JsonObject();
            gameConfigEntry.addProperty("type", game.getType().getName());

            JsonObject gameConfig = gson.toJsonTree(game.getConfig()).getAsJsonObject();
            gameConfigEntry.add("config", gameConfig);

            gamesConfig.add(game.getWorld().getName(), gameConfigEntry);
        }
        games.clear();

        FileWriter writer;
        try {
            writer = new FileWriter(configFile);
        } catch (IOException e) {
            throw new GamesSaveFailedException("Failed to write to games.json", e);
        }

        new GsonBuilder().setPrettyPrinting().create().toJson(gamesConfig, writer);
    }

    public Optional<Game<?, ?>> getGameForWorld(World world) {
        return games.stream().filter(game -> game.getWorld().equals(world)).findFirst();
    }

    public <G extends Game<G, C>, C> void createGame(World world, GameType<G, C> type) {
        if (games.stream().anyMatch(game -> game.getWorld().equals(world))) {
            return;
        }

        C config = type.getDefaultConfigCreator().get();
        G game = type.getCreator().createGame(plugin, world, config);

        games.add(game);
        game.handleGameLoaded();
    }

    public void deleteGame(World world) throws GamesSaveFailedException {
        Optional<Game<?, ?>> game = games.stream().filter(g -> !g.getWorld().equals(world)).findFirst();
        if (game.isPresent()) {
            games.remove(game.get());
            game.get().handleGameUnloaded();
            saveGames();
        }
    }

    public void startGame(Game<?, ?> game) {
        if (game.isRunning()) return;

        game.setRunning(true);
        game.handleGameStarted();
    }

    public void endGame(Game<?, ?> game) {
        if (!game.isRunning()) return;

        game.setRunning(false);
        game.handleGameEnded();
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
