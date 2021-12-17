package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;

public class LlamaSaysCommand extends GameCommand {
    public LlamaSaysCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);

        addSubCommand("floorCenter", createBlockConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The floor center is currently at %s".formatted(config.floorCenter),
                (config, position) -> config.floorCenter = position,
                "Floor center successfully changed"
        ));
        addSubCommand("numberOfRounds", createNumberConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The game will end after %d rounds".formatted(config.numberOfRounds),
                (config, rounds) -> config.numberOfRounds = rounds,
                "Number of rounds successfully changed"
        ));
        addSubCommand("floor", createAreaConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The floor is currently at %s".formatted(config.floor),
                (config, floor) -> config.floor = floor,
                "The position of the floor was successfully changed"
        ));
    }
}
