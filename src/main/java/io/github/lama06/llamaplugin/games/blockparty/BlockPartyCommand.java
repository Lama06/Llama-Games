package io.github.lama06.llamaplugin.games.blockparty;

import io.github.lama06.llamaplugin.command.CollectionCommand;
import io.github.lama06.llamaplugin.command.Require;
import io.github.lama06.llamaplugin.games.GameCommand;
import io.github.lama06.llamaplugin.games.GamesModule;
import io.github.lama06.llamaplugin.util.BlockArea;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class BlockPartyCommand extends GameCommand {
    public BlockPartyCommand(GamesModule module) {
        super(module, "blockparty");

        addSubCommand("floor", createBlockAreaConfigSubCommand(
                module,
                BlockPartyGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.getFloor())),
                BlockPartyConfig::setFloor,
                floor -> Component.text("The floor is now at %s".formatted(floor))
        ));

        addSubCommand("deadlyBlock", createMaterialConfigSubCommand(
                module,
                BlockPartyGame.class,
                config -> Component.text("The deadly block is currently set to ").append(Component.translatable(config.getDeadlyBlock())),
                BlockPartyConfig::setDeadlyBlock,
                material -> Component.text("The deadly block is now set to ").append(Component.translatable(material))
        ));

        addSubCommand("floors", createCollectionConfigSubCommand(
                module,
                BlockPartyGame.class,
                BlockPartyConfig::getFloors,
                CollectionCommand.listByMappingElements(Component.text("There are no floors"), floor -> Component.text("%s: %s".formatted(floor.getName(), floor.getArea()))),
                CollectionCommand.forbidElementsWithSameName((sender, args) -> {
                    if (!Require.argsExact(sender, args, 7)) return Optional.empty();
                    String name = args[0];

                    Optional<BlockArea> area = Require.blockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (area.isEmpty()) return Optional.empty();

                    return Optional.of(new Floor(name, area.get()));
                }, Component.text("Successfully added a new floor"), Component.text("A floor with that name already exists")),
                CollectionCommand.removeElementsByName(Component.text("This floor was successfully removed"), Component.text("No floor with this name exists"))
        ));
    }
}
