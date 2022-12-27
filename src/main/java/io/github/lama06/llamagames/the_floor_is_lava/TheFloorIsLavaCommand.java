package io.github.lama06.llamagames.the_floor_is_lava;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockArea;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class TheFloorIsLavaCommand extends GameCommand {
    public TheFloorIsLavaCommand(LlamaGamesPlugin plugin) {
        super(plugin, "thefloorislava");

        addSubCommand("floors", createCollectionConfigSubCommand(
                plugin,
                TheFloorIsLavaGame.class,
                config -> config.floors,
                new MapElementsListStrategy<>(Component.text("There are no floors"), floor -> Component.text("%s -> %s".formatted(floor.name, floor.blocks))),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 7)) return Optional.empty();

                    String name = args[0];

                    Optional<BlockArea> area = requireBlockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (area.isEmpty()) return Optional.empty();

                    return Optional.of(new Floor(name, area.get()));
                }, Component.text("The floor was successfully added"), Component.text("A floor with this name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("Successfully removed the floor with this name"), Component.text("There is no floor with this name"))
        ));

        addSubCommand("blockStates", createCollectionConfigSubCommand(
                plugin,
                TheFloorIsLavaGame.class,
                config -> config.blockStates,
                new MapElementsListStrategy<>(Component.text("There are no block states"), state -> Component.text(state.toString())),
                new SimpleAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 1)) return Optional.empty();
                    return requireBlockData(sender, args[0]);
                }, Component.text("Successfully added the block state")),
                new RemoveByListIndexStrategy<>(Component.text("The block state was successfully removed"))
        ));

        addSubCommand("blockAgeTime", createIntegerConfigSubCommand(
                plugin,
                TheFloorIsLavaGame.class,
                config -> Component.text("Floor blocks will age after %d ticks".formatted(config.blockAgeTime)),
                (config, ageTime) -> config.blockAgeTime = ageTime,
                ageTime -> Component.text("Blocks will now age after %d ticks".formatted(ageTime))
        ));

        addSubCommand("deadlyBlock", createMaterialConfigSubCommand(
                plugin,
                TheFloorIsLavaGame.class,
                config -> Component.text("Players will die if they touch: ").append(Component.translatable(config.deadlyBlock)),
                (config, deadlyBlock) -> config.deadlyBlock = deadlyBlock,
                deadlyBlock -> Component.text("Players will now die if they touch: ").append(Component.translatable(deadlyBlock))
        ));
    }
}
