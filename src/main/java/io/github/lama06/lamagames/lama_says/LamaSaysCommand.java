package io.github.lama06.lamagames.lama_says;

import io.github.lama06.lamagames.GameCommand;
import io.github.lama06.lamagames.LamaGamesPlugin;

public class LamaSaysCommand extends GameCommand {
    public LamaSaysCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);

        addSubCommand("floorCenter", createBlockConfigSubCommand(
                plugin,
                LamaSaysGame.class,
                config -> "The floor center is currently at %s".formatted(config.floorCenter),
                (config, position) -> config.floorCenter = position,
                "Floor center successfully changed"
        ));
        addSubCommand("numberOfRounds", createNumberConfigSubCommand(
                plugin,
                LamaSaysGame.class,
                config -> "The game will end after %d rounds".formatted(config.numberOfRounds),
                (config, rounds) -> config.numberOfRounds = rounds,
                "Number of rounds successfully changed"
        ));
        addSubCommand("floor", createAreaConfigSubCommand(
                plugin,
                LamaSaysGame.class,
                config -> "The floor is currently at %s".formatted(config.floor),
                (config, floor) -> config.floor = floor,
                "The position of the floor was successfully changed"
        ));
    }
}
