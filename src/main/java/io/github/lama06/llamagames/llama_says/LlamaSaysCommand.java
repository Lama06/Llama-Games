package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;

public class LlamaSaysCommand extends GameCommand {
    public LlamaSaysCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);

        addSubCommand("floorCenter", LlamaCommand.createBlockPositionConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The floor center is currently at %s".formatted(config.floorCenter),
                (config, position) -> config.floorCenter = position,
                "Floor center successfully changed to %s"::formatted
        ));
        addSubCommand("numberOfRounds", createIntegerConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The game will end after %d rounds".formatted(config.numberOfRounds),
                (config, rounds) -> config.numberOfRounds = rounds,
                "Number of rounds successfully changed to %d"::formatted
        ));
        addSubCommand("floor", createBlockAreaConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> "The floor is currently at %s".formatted(config.floor),
                (config, floor) -> config.floor = floor,
                "The position of the floor was successfully changed to %s"::formatted
        ));
    }
}
