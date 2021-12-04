package io.github.lama06.lamagames;

public abstract class GameCommand extends LamaCommand {
    public GameCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("setSpawn", createBlockConfigChangeSubCommand(
                plugin,
                null,
                (config, position) -> config.spawnPoint = position,
                "Spawn point successfully changed"
        ));
    }
}
