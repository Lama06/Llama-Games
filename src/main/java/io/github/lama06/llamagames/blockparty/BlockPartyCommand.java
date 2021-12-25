package io.github.lama06.llamagames.blockparty;

import io.github.lama06.llamagames.LlamaCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockArea;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class BlockPartyCommand extends LlamaCommand {
    public BlockPartyCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("floor", createBlockAreaConfigSubCommand(
                plugin,
                BlockPartyGame.class,
                config -> Component.text("The floor is currently at %s".formatted(config.floor)),
                (config, floor) -> config.floor = floor,
                floor -> Component.text("The floor is now at %s".formatted(floor))
        ));
        addSubCommand("deadlyBlock", createMaterialConfigSubCommand(
                plugin,
                BlockPartyGame.class,
                config -> Component.text("The deadly block is currently set to ").append(Component.translatable(config.deadlyBlock)),
                (config, material) -> config.deadlyBlock = material,
                material -> Component.text("The deadly block is now set to ").append(Component.translatable(material))
        ));
        addSubCommand("addFloor", this::addFloor);
        addSubCommand("removeFloor", this::removeFloor);
        addSubCommand("listFloors", this::listFloors);
    }

    public void addFloor(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 8)) return;

        Optional<BlockPartyGame> game = requireGame(plugin, sender, args[0], BlockPartyGame.class);
        if (game.isEmpty()) return;

        String name = args[1];

        Optional<BlockArea> area = requireBlockArea(sender, args[2], args[3], args[4], args[5], args[6], args[7]);
        if (area.isEmpty()) return;

        if (game.get().getConfig().getFloorByName(name).isPresent()) {
            sender.sendMessage(Component.text("A floor with that name already exists", NamedTextColor.RED));
            return;
        }

        game.get().getConfig().floors.add(new Floor(name, area.get()));
    }

    public void removeFloor(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 2)) return;

        Optional<BlockPartyGame> game = requireGame(plugin, sender, args[0], BlockPartyGame.class);
        if (game.isEmpty()) return;

        String name = args[1];

        Optional<Floor> floor = game.get().getConfig().getFloorByName(name);
        if (floor.isEmpty()) {
            sender.sendMessage(Component.text("No floor with this name exists", NamedTextColor.RED));
            return;
        }

        game.get().getConfig().floors.remove(floor.get());
        sender.sendMessage(Component.text("The floor was removed", NamedTextColor.GREEN));
    }

    public void listFloors(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<BlockPartyGame> game = requireGame(plugin, sender, args[0], BlockPartyGame.class);
        if (game.isEmpty()) return;

        if (game.get().getConfig().floors.isEmpty()) {
            sender.sendMessage("There are no floors");
            return;
        }

        TextComponent.Builder text = Component.text();
        boolean first = true;
        for (Floor floor : game.get().getConfig().floors) {
            if (first) {
                first = false;
            } else {
                text.append(Component.newline());
            }

            text.append(Component.text("%s: %s".formatted(floor.name, floor.area)));
        }

        sender.sendMessage(text);
    }
}
