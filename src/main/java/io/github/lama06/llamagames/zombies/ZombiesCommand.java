package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import net.kyori.adventure.text.Component;

public class ZombiesCommand extends GameCommand {
    public ZombiesCommand(LlamaGamesPlugin plugin) {
        super(plugin, "zombies");
        addSubCommand("startArea", createStringSubCommand(
                plugin,
                ZombiesGame.class,
                config -> Component.text("The start area is currently set to %s".formatted(config.startArea)),
                (config, name) -> config.startArea = name,
                name -> Component.text("The start area is now set to %s".formatted(name))
        ));
    }
}
