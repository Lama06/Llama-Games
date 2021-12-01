package io.github.lama06.lamagames;

public abstract class GameCommand extends LamaCommand {
    @SuppressWarnings("unchecked")
    public <G extends Game<G, C>, C extends GameConfig> GameCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("setSpawn", createBlockConfigChangeSubCommand(
                plugin,
                (Class<G>) Game.class,
                (config, position) -> config.spawnPoint = position,
                "Spawn point successfully changed"
        ));
    }
}
