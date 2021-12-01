package io.github.lama06.lamagames.lama_says;

import io.github.lama06.lamagames.GameCommand;
import io.github.lama06.lamagames.LamaGamesPlugin;
import io.github.lama06.lamagames.util.Area;
import io.github.lama06.lamagames.util.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class LamaSaysCommand extends GameCommand {
    public LamaSaysCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("setFloorCenter", createBlockConfigChangeSubCommand(
                plugin,
                LamaSaysGame.class,
                (config, position) -> config.floorCenter = position,
                "Floor center successfully changed"
        ));
        addSubCommand("setNumberOfRounds", createNumberConfigChangeSubCommand(
                plugin,
                LamaSaysGame.class,
                (config, rounds) -> config.numberOfRounds = rounds,
                "Number of rounds successfully changed"
        ));
        addSubCommand("setFloor", this::setFloor);
    }

    public void setFloor(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 7)) return;

        Optional<LamaSaysGame> game = requireGame(plugin, sender, args[0], LamaSaysGame.class);
        if (game.isEmpty()) return;

        Optional<BlockPosition> position1 = requirePosition(sender, args[1], args[2], args[3]);
        if (position1.isEmpty()) return;

        Optional<BlockPosition> position2 = requirePosition(sender, args[4], args[5], args[6]);
        if (position2.isEmpty()) return;

        game.get().getConfig().floor = new Area(position1.get(), position2.get());
        sender.sendMessage(Component.text("The floor has been successfully changed").color(NamedTextColor.GREEN));
    }
}
