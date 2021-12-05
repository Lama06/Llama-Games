package io.github.lama06.lamagames;

public abstract class GameCommand extends LamaCommand {
    public GameCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("spawn", createBlockConfigSubCommand(
                plugin,
                null,
                config -> "The spawn is currently at %s".formatted(config.spawnPoint),
                (config, position) -> config.spawnPoint = position,
                "Spawn point successfully changed"
        ));
        addSubCommand("cancelEvents", createBooleanConfigSubCommand(
                plugin,
                null,
                config -> config.cancelEvents ?
                        "All events in this game world a being canceled" :
                        "Events are not being canceled in this game world",
                (config, flag) -> config.cancelEvents = flag,
                "The cancel events flag was successfully changed"
        ));
    }
}
