package io.github.lama06.llamaplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class LlamaPlugin extends JavaPlugin {
    private ModuleManager modules;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        new LlamaPluginCommand(this);

        modules = new ModuleManager(this);

        modules.onEnable();
    }

    @Override
    public void onDisable() {
        modules.onDisable();
    }

    public ModuleManager getModules() {
        return modules;
    }
}
