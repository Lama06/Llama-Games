package io.github.lama06.llamagames;

public abstract class GameCommand extends LlamaCommand {
    public GameCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("spawn", createEntityPositionSubCommand(
                plugin,
                null,
                config -> "The spawn is currently at %s".formatted(config.spawnPoint),
                (config, position) -> config.spawnPoint = position,
                "Spawn point successfully changed to %s"::formatted
        ));
        addSubCommand("cancelEvents", createBooleanConfigSubCommand(
                plugin,
                null,
                config -> config.cancelEvents ?
                        "All events in this game world are being canceled" :
                        "Events are not being canceled in this game world",
                (config, flag) -> config.cancelEvents = flag,
                flag -> flag ?
                        "Events in this world are being canceled" :
                        "Events in this world are not being canceled"
        ));
    }
}
