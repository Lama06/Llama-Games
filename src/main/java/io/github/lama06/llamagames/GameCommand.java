package io.github.lama06.llamagames;

import net.kyori.adventure.text.Component;

public abstract class GameCommand extends LlamaCommand {
    public GameCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("spawn", createEntityPositionConfigSubCommand(
                plugin,
                null,
                config -> Component.text("The spawn is currently at %s".formatted(config.spawnPoint)),
                (config, position) -> config.spawnPoint = position,
                position -> Component.text("Spawn point successfully changed to %s".formatted(position))
        ));
        addSubCommand("cancelEvents", createBooleanConfigSubCommand(
                plugin,
                null,
                config -> config.cancelEvents ?
                        Component.text("All events in this game world are being canceled") :
                        Component.text("Events are not being canceled in this game world"),
                (config, flag) -> config.cancelEvents = flag,
                flag -> flag ?
                        Component.text("Events in this world are being canceled") :
                        Component.text("Events in this world are not being canceled")
        ));
    }
}
