package io.github.lama06.llamagames.llama_says;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import net.kyori.adventure.text.Component;

public class LlamaSaysCommand extends GameCommand {
    public LlamaSaysCommand(LlamaGamesPlugin plugin) {
        super(plugin, "llamasays");

        addSubCommand("floorCenter", LlamaCommand.createBlockPositionConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The floor center is currently at %s".formatted(config.floorCenter)),
                (config, position) -> config.floorCenter = position,
                position -> Component.text("Floor center successfully changed to %s".formatted(position))
        ));
        addSubCommand("numberOfRounds", createIntegerConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The game will end after %d rounds".formatted(config.numberOfRounds)),
                (config, rounds) -> config.numberOfRounds = rounds,
                rounds -> Component.text("Number of rounds successfully changed to %d".formatted(rounds))
        ));
        addSubCommand("floor", createBlockAreaConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.floor)),
                (config, floor) -> config.floor = floor,
                floor -> Component.text("The position of the floor was successfully changed to %s".formatted(floor))
        ));
    }
}
