package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockArea;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class BlockPartyCommand extends GameCommand {
    public BlockPartyCommand(LlamaGamesPlugin plugin) {
        super(plugin, "blockparty");

        addSubCommand("floor", createBlockAreaConfigSubCommand(
                plugin,
                BlockPartyGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.getFloor())),
                BlockPartyConfig::setFloor,
                floor -> Component.text("The floor is now at %s".formatted(floor))
        ));

        addSubCommand("deadlyBlock", createMaterialConfigSubCommand(
                plugin,
                BlockPartyGame.class,
                config -> Component.text("The deadly block is currently set to ").append(Component.translatable(config.getDeadlyBlock())),
                BlockPartyConfig::setDeadlyBlock,
                material -> Component.text("The deadly block is now set to ").append(Component.translatable(material))
        ));

        addSubCommand("floors", createCollectionConfigSubCommand(
                plugin,
                BlockPartyGame.class,
                BlockPartyConfig::getFloors,
                new MapElementsListStrategy<>(Component.text("There are no floors"), floor -> Component.text("%s: %s".formatted(floor.getName(), floor.getArea()))),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 7)) return Optional.empty();
                    String name = args[0];

                    Optional<BlockArea> area = requireBlockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (area.isEmpty()) return Optional.empty();

                    return Optional.of(new Floor(name, area.get()));
                }, Component.text("Successfully added a new floor"), Component.text("A floor with that name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("This floor was successfully removed"), Component.text("No floor with this name exists"))
        ));
    }
}
