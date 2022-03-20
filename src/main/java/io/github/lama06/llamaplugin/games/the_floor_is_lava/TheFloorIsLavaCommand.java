package io.github.lama06.llamaplugin.games.the_floor_is_lava;

import io.github.lama06.llamaplugin.command.CollectionCommand;
import io.github.lama06.llamaplugin.command.Require;
import io.github.lama06.llamaplugin.games.GameCommand;
import io.github.lama06.llamaplugin.games.GamesModule;
import io.github.lama06.llamaplugin.util.BlockArea;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class TheFloorIsLavaCommand extends GameCommand {
    public TheFloorIsLavaCommand(GamesModule module) {
        super(module, "thefloorislava");

        addSubCommand("floors", createCollectionConfigSubCommand(
                module,
                TheFloorIsLavaGame.class,
                config -> config.floors,
                CollectionCommand.listByMappingElements(Component.text("There are no floors"), floor -> Component.text("%s -> %s".formatted(floor.name, floor.blocks))),
                CollectionCommand.forbidElementsWithSameName((sender, args) -> {
                    if (!Require.argsExact(sender, args, 7)) return Optional.empty();

                    String name = args[0];

                    Optional<BlockArea> area = Require.blockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (area.isEmpty()) return Optional.empty();

                    return Optional.of(new Floor(name, area.get()));
                }, Component.text("The floor was successfully added"), Component.text("A floor with this name already exists")),
                CollectionCommand.removeElementsByName(Component.text("Successfully removed the floor with this name"), Component.text("There is no floor with this name"))
        ));

        addSubCommand("blockStates", createCollectionConfigSubCommand(
                module,
                TheFloorIsLavaGame.class,
                config -> config.blockStates,
                CollectionCommand.listByMappingElements(Component.text("There are no block states"), state -> Component.text(state.toString())),
                CollectionCommand.addToList((sender, args) -> {
                    if (!Require.argsExact(sender, args, 1)) return Optional.empty();
                    return Require.blockData(sender, args[0]);
                }, Component.text("Successfully added the block state")),
                CollectionCommand.removeByListIndex(Component.text("The block state was successfully removed"))
        ));

        addSubCommand("blockAgeTime", createIntegerConfigSubCommand(
                module,
                TheFloorIsLavaGame.class,
                config -> Component.text("Floor blocks will age after %d ticks".formatted(config.blockAgeTime)),
                (config, ageTime) -> config.blockAgeTime = ageTime,
                ageTime -> Component.text("Blocks will now age after %d ticks".formatted(ageTime))
        ));

        addSubCommand("deadlyBlock", createMaterialConfigSubCommand(
                module,
                TheFloorIsLavaGame.class,
                config -> Component.text("Players will die if they touch: ").append(Component.translatable(config.deadlyBlock)),
                (config, deadlyBlock) -> config.deadlyBlock = deadlyBlock,
                deadlyBlock -> Component.text("Players will now die if they touch: ").append(Component.translatable(deadlyBlock))
        ));
    }
}
