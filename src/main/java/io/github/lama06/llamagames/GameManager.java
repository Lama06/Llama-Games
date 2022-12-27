package io.github.lama06.llamagames;

import com.google.common.io.Files;
import com.google.gson.*;
import io.github.lama06.llamagames.util.BlockDataTypeAdapter;
import io.github.lama06.llamagames.util.MaterialTypeAdapter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class GameManager implements Listener {
    private static final Map<Class<?>, TypeAdapter<?>> DEFAULT_TYPE_ADAPTERS = Map.ofEntries(
            Map.entry(BlockData.class, new BlockDataTypeAdapter()),
            Map.entry(Material.class, new MaterialTypeAdapter())
    );

    private final LlamaGamesPlugin plugin;
    private final Logger logger;
    private final Gson gson = createGson();
    private final File configFile;
    private final Set<Game<?, ?>> games = new HashSet<>();
    private final Map<String, JsonObject> invalidGames = new HashMap<>();
    private boolean configCorrectlyLoaded = false;

    public GameManager(LlamaGamesPlugin plugin) {
        this.plugin = plugin;
        logger = plugin.getSLF4JLogger();
        this.configFile = new File(plugin.getDataFolder(), "games.json");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private Gson createGson() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting().serializeNulls().enableComplexMapKeySerialization();

        for (GameType<?, ?> type : GameType.getValues()) {
            Map<Class<?>, TypeAdapter<?>> typeAdapters = type.getTypeAdapters();

            if (typeAdapters == null) {
                continue;
            }

            for (Map.Entry<Class<?>, TypeAdapter<?>> typeAdapter : typeAdapters.entrySet()) {
                builder.registerTypeHierarchyAdapter(typeAdapter.getKey(), typeAdapter.getValue());
            }
        }

        for (Map.Entry<Class<?>, TypeAdapter<?>> typeAdapter : DEFAULT_TYPE_ADAPTERS.entrySet()) {
            builder.registerTypeHierarchyAdapter(typeAdapter.getKey(), typeAdapter.getValue());
        }

        return builder.create();
    }

    private File getConfigBackupFolder() {
        File folder = new File(plugin.getDataFolder(), "backups");
        if (folder.exists() && !folder.isDirectory()) {
            logger.error("Failed to backup the game config file because there is a file named backup in the config directory. No Backups will be created!");
            return null;
        }

        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder;
    }

    public void backupConfigFile() {
        if (!configFile.exists()) {
            return;
        }

        File backupFolder = getConfigBackupFolder();
        if (backupFolder == null) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss");
        String time = formatter.format(LocalDateTime.now());
        File backupFile = new File(backupFolder, "Backup (%s).json".formatted(time));
        if (backupFile.exists()) {
            return;
        }

        try {
            backupFile.createNewFile();

            Files.copy(configFile, backupFile);

            logger.info("A backup of the config file was successfully created");
        } catch (IOException e) {
            logger.error("Failed to backup the config file: %s".formatted(e));
        }
    }

    private JsonObject loadGamesConfig() {
        boolean created;

        try {
            created = configFile.createNewFile();
        } catch (IOException e) {
            logger.error("Failed to create the config file: %s".formatted(e.getMessage()));
            return null;
        }

        if (created) {
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("{}");

                return new JsonObject();
            } catch (IOException e) {
                logger.error("Failed to write to the config file: %s".formatted(e));
                return null;
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonElement gamesConfig = JsonParser.parseReader(reader);

            if (!gamesConfig.isJsonObject()) {
                logger.error("The games config file does not contain a root object");
                return null;
            }

            return gamesConfig.getAsJsonObject();
        } catch (IOException e) {
            logger.error("Failed to load games config file: %s".formatted(e));
            return null;
        } catch (JsonParseException e) {
            logger.error("Failed to parse games config file: %s".formatted(e));
            return null;
        }
    }

    private <G extends Game<G, C>, C extends GameConfig> void loadGame(GameType<G, C> type, World world, JsonObject config, JsonObject gameConfigRoot) {
        C deserializedConfig;

        try {
            deserializedConfig = gson.fromJson(config, type.getConfigType());
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse the config of the game in the world %s: %s".formatted(world.getName(), e));
            invalidGames.put(world.getName(), gameConfigRoot);
            return;
        }

        G game = type.getCreator().createGame(plugin, world, deserializedConfig, type);
        games.add(game);

        game.loadGame();
    }

    public boolean loadGames() {
        JsonObject gamesConfig = loadGamesConfig();
        if (gamesConfig == null) {
            return false;
        }

        for (Map.Entry<String, JsonElement> gameEntry : gamesConfig.entrySet()) {
            if (gameEntry.getKey().equals("dataVersion")) {
                continue;
            }

            if (!gameEntry.getValue().isJsonObject()) {
                logger.error("The games config file has an invalid format");
                continue;
            }
            JsonObject gameConfigRoot = gameEntry.getValue().getAsJsonObject();

            String worldName = gameEntry.getKey();
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                logger.error("Cannot find a world named %s".formatted(worldName));
                invalidGames.put(gameEntry.getKey(), gameConfigRoot);
                continue;
            }

            if (!gameConfigRoot.has("type") ||
                    !gameConfigRoot.get("type").isJsonPrimitive() ||
                    !gameConfigRoot.get("type").getAsJsonPrimitive().isString()) {
                logger.error("The games config file contains a game without a type attribute");
                invalidGames.put(gameEntry.getKey(), gameConfigRoot);
                continue;
            }
            String gameTypeName = gameEntry.getValue().getAsJsonObject().get("type").getAsString();
            Optional<GameType<?, ?>> type = GameType.getByName(gameTypeName);
            if (type.isEmpty()) {
                logger.error(String.format("Could not find game type: %s", gameTypeName));
                invalidGames.put(gameEntry.getKey(), gameConfigRoot);
                continue;
            }

            if (!gameConfigRoot.has("config") || !gameConfigRoot.get("config").isJsonObject()) {
                logger.error("The game config file contains a game without a config attribute");
                invalidGames.put(gameEntry.getKey(), gameConfigRoot);
                continue;
            }
            JsonObject gameConfig = gameConfigRoot.get("config").getAsJsonObject();

            loadGame(type.get(), world, gameConfig, gameConfigRoot);
        }

        configCorrectlyLoaded = true;
        return true;
    }

    public boolean saveGameConfig() {
        if (!configCorrectlyLoaded) {
            return false;
        }

        JsonObject gamesConfig = new JsonObject();
        gamesConfig.addProperty("dataVersion", 1);

        for (Game<?, ?> game : games) {
            JsonObject gameConfigEntry = new JsonObject();
            gameConfigEntry.addProperty("type", game.getType().getName());

            JsonObject gameConfig = gson.toJsonTree(game.getConfig()).getAsJsonObject();
            gameConfigEntry.add("config", gameConfig);

            gamesConfig.add(game.getWorld().getName(), gameConfigEntry);
        }

        for (Map.Entry<String, JsonObject> entry : invalidGames.entrySet()) {
            gamesConfig.add(entry.getKey(), entry.getValue());
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(gamesConfig, writer);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public boolean saveGameConfig(Audience errorListener) {
        boolean successful = saveGameConfig();
        if (!successful) {
            errorListener.sendMessage(Component.text("Failed to save the config file", NamedTextColor.RED));
        }
        return successful;
    }

    public void unloadGames() {
        for (Game<?, ?> game : games) {
            game.unloadGame();
        }

        games.clear();
    }

    public Optional<Game<?, ?>> getGameForWorld(World world) {
        return games.stream().filter(game -> game.getWorld().equals(world)).findFirst();
    }

    public <G extends Game<G, C>, C extends GameConfig> void createGame(World world, GameType<G, C> type) {
        if (getGameForWorld(world).isPresent()) {
            return;
        }

        C config = type.getDefaultConfigCreator().get();
        G game = type.getCreator().createGame(plugin, world, config, type);

        games.add(game);
        game.loadGame();
    }

    public boolean deleteGame(World world) {
        Optional<Game<?, ?>> game = getGameForWorld(world);
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
}
