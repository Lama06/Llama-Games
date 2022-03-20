package io.github.lama06.llamaplugin.games.llama_says;

import io.github.lama06.llamaplugin.games.GameCommand;
import io.github.lama06.llamaplugin.games.GamesModule;
import net.kyori.adventure.text.Component;

public class LlamaSaysCommand extends GameCommand {
    public LlamaSaysCommand(GamesModule module) {
        super(module, "llamasays");

        addSubCommand("floorCenter", GameCommand.createBlockPositionConfigSubCommand(
                module,
                LlamaSaysGame.class,
                config -> Component.text("The floor center is currently at %s".formatted(config.getFloorCenter())),
                LlamaSaysConfig::setFloorCenter,
                position -> Component.text("Floor center successfully changed to %s".formatted(position))
        ));

        addSubCommand("numberOfRounds", createIntegerConfigSubCommand(
                module,
                LlamaSaysGame.class,
                config -> Component.text("The game will end after %d rounds".formatted(config.getNumberOfRounds())),
                LlamaSaysConfig::setNumberOfRounds,
                rounds -> Component.text("Number of rounds successfully changed to %d".formatted(rounds))
        ));

        addSubCommand("floor", createBlockAreaConfigSubCommand(
                module,
                LlamaSaysGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.getFloor())),
                LlamaSaysConfig::setFloor,
                floor -> Component.text("The position of the floor was successfully changed to %s".formatted(floor))
        ));

        addSubCommand("floorBlock", createMaterialConfigSubCommand(
                module,
                LlamaSaysGame.class,
                config -> Component.text("The floor is currently made of ").append(Component.translatable(config.getFloorMaterial())),
                LlamaSaysConfig::setFloorMaterial,
                material -> Component.text("The floor is now made of ").append(Component.translatable(material))
        ));
    }
}
