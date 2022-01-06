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
                config -> Component.text("The floor center is currently at %s".formatted(config.getFloorCenter())),
                LlamaSaysConfig::setFloorCenter,
                position -> Component.text("Floor center successfully changed to %s".formatted(position))
        ));

        addSubCommand("numberOfRounds", createIntegerConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The game will end after %d rounds".formatted(config.getNumberOfRounds())),
                LlamaSaysConfig::setNumberOfRounds,
                rounds -> Component.text("Number of rounds successfully changed to %d".formatted(rounds))
        ));

        addSubCommand("floor", createBlockAreaConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.getFloor())),
                LlamaSaysConfig::setFloor,
                floor -> Component.text("The position of the floor was successfully changed to %s".formatted(floor))
        ));

        addSubCommand("floorBlock", createMaterialConfigSubCommand(
                plugin,
                LlamaSaysGame.class,
                config -> Component.text("The floor is currently made of ").append(Component.translatable(config.getFloorMaterial())),
                LlamaSaysConfig::setFloorMaterial,
                material -> Component.text("The floor is now made of ").append(Component.translatable(material))
        ));
    }
}
