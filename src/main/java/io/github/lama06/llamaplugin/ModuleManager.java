package io.github.lama06.llamaplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.github.lama06.llamaplugin.util.GsonConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ModuleManager {
    private final LlamaPlugin plugin;
    private Gson gson;
    private final File configFile;
    private Config config;
    private Set<Module<?>> enabledModules;

    public ModuleManager(LlamaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "modules.json");
    }

    private void createGson() {
        gson = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(ModuleType.class, new ModuleTypeAdapter()).create();
    }

    private void loadConfig() throws ConfigLoadFailedException {
        boolean created;

        try {
            created = configFile.createNewFile();
        } catch (IOException e) {
            throw new ConfigLoadFailedException("Failed to create the modules config file: %s".formatted(e.getMessage()));
        }

        if (created) {
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("{}");
                config = new Config();
                return;
            } catch (IOException e) {
                throw new ConfigLoadFailedException("Failed to write to the modules config file: %s".formatted(e));
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            throw new ConfigLoadFailedException("Failed to load modules config file: %s".formatted(e));
        } catch (JsonParseException e) {
            throw new ConfigLoadFailedException("Failed to parse modules config file: %s".formatted(e));
        }
    }

    private <T extends Module<T>> void loadModule(ModuleType<T> type) throws ModuleEnableFailedException {
        T module = type.creator().createModule(plugin, type);
        module.onEnable();
        enabledModules.add(module);
    }

    private void safeConfig() throws ConfigSaveFailedException {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            throw new ConfigSaveFailedException("Failed to write to config file", e);
        }
    }

    public void onEnable() {
        createGson();

        try {
            loadConfig();
        } catch (ConfigLoadFailedException e) {
            plugin.getSLF4JLogger().error("Failed to load the modules config file: %s".formatted(e));
            return;
        }

        enabledModules = new HashSet<>();
        for (ModuleType<?> type : config.enabledModules) {
            try {
                loadModule(type);
            } catch (ModuleEnableFailedException e) {
                plugin.getSLF4JLogger().error("Failed to enable module %s: %s".formatted(type.name(), e));
            }
        }
    }

    public void onDisable() {
        try {
            safeConfig();
        } catch (ConfigSaveFailedException e) {
            plugin.getSLF4JLogger().error(e.getMessage());
        }

        for (Module<?> enabledModule : enabledModules) {
            enabledModule.onDisable();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Module<T>> T getModule(ModuleType<T> type) {
        for (Module<?> enabledModule : enabledModules) {
            if (enabledModule.getType().equals(type)) {
                return (T) enabledModule;
            }
        }

        return null;
    }

    public Set<Module<?>> getEnabledModules() {
        return enabledModules;
    }

    public Config getConfig() {
        return config;
    }

    public static class Config {
        public Set<ModuleType<?>> enabledModules = new HashSet<>();

        @GsonConstructor
        public Config() { }
    }

    public static class ConfigLoadFailedException extends Exception {
        public ConfigLoadFailedException(String message) {
            super(message);
        }
    }

    public static class ConfigSaveFailedException extends Exception {
        public ConfigSaveFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
