package io.github.lama06.llamagames;

import net.kyori.adventure.text.Component;

public abstract class GameCommand extends LlamaCommand {
    public GameCommand(LlamaGamesPlugin plugin, String name) {
        super(plugin, name);

        addSubCommand("spawn", createEntityPositionConfigSubCommand(
                plugin,
                null,
                config -> Component.text("The spawn is currently at %s".formatted(config.getSpawnPoint())),
                GameConfig::setSpawnPoint,
                position -> Component.text("Spawn point successfully changed to %s".formatted(position))
        ));

        addSubCommand("cancelEvents", createBooleanConfigSubCommand(
                plugin,
                null,
                config -> config.isCancelEvents() ?
                        Component.text("All events in this game world are being canceled") :
                        Component.text("Events are not being canceled in this game world"),
                GameConfig::setCancelEvents,
                flag -> flag ?
                        Component.text("Events in this world will now be canceled") :
                        Component.text("Events in this world will no longer be canceled")
        ));

        addSubCommand("doNotCancelOpEvents", createBooleanConfigSubCommand(
                plugin,
                null,
                config -> config.isDoNotCancelOpEvents() ?
                        Component.text("Events are allowed to be performed by operators") :
                        Component.text("Events are not allowed to be performed by operators"),
                GameConfig::setDoNotCancelOpEvents,
                flag -> flag ?
                        Component.text("Operators are now allowed to perform events") :
                        Component.text("Operators are no longer allowed to perform events")
        ));
    }
}
