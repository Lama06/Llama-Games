package io.github.lama06.llamaplugin;

public abstract class Module<T extends Module<T>> {
    protected final LlamaPlugin plugin;
    protected final ModuleType<T> type;

    public Module(LlamaPlugin plugin, ModuleType<T> type) {
        this.plugin = plugin;
        this.type = type;
    }

    public abstract void onEnable() throws ModuleEnableFailedException;

    public abstract void onDisable();

    public LlamaPlugin getPlugin() {
        return plugin;
    }

    public ModuleType<T> getType() {
        return type;
    }
}
