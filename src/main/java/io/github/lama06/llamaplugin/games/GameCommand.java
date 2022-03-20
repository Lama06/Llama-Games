package io.github.lama06.llamaplugin.games;

import io.github.lama06.llamaplugin.command.CollectionCommand;
import io.github.lama06.llamaplugin.command.ConfigCommand;
import io.github.lama06.llamaplugin.command.LlamaCommand;
import io.github.lama06.llamaplugin.command.Require;
import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.EntityPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GameCommand extends LlamaCommand {
    public GameCommand(GamesModule module, String name) {
        super(module.getPlugin(), name);

        addSubCommand("spawn", createEntityPositionConfigSubCommand(
                module,
                null,
                config -> Component.text("The spawn is currently at %s".formatted(config.getSpawnPoint())),
                GameConfig::setSpawnPoint,
                position -> Component.text("Spawn point successfully changed to %s".formatted(position))
        ));

        addSubCommand("cancelEvents", createBooleanConfigSubCommand(
                module,
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
                module,
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

    public static Optional<Game<?, ?>> requireGame(GamesModule module, CommandSender sender, String worldName) {
        Optional<World> world = Require.world(sender, worldName);
        if (world.isEmpty()) return Optional.empty();

        Optional<Game<?, ?>> game = module.getGameManager().getGameForWorld(world.get());
        if (game.isEmpty()) {
            sender.sendMessage(Component.text("No game exists in this world").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return game;
    }

    @SuppressWarnings("unchecked")
    public static <G extends Game<G, ?>> Optional<G> requireGame(
            GamesModule module,
            CommandSender sender,
            String worldName,
            Class<G> gameType
    ) {
        Optional<Game<?, ?>> game = requireGame(module, sender, worldName);
        if (game.isEmpty()) return Optional.empty();

        if (gameType != null && !gameType.isAssignableFrom(game.get().getClass())) {
            sender.sendMessage(Component.text("This world contains a game that is not of the right type").color(NamedTextColor.RED));
            return Optional.empty();
        }

        return Optional.of((G) game.get());
    }

    public static <GameType extends Game<GameType, ConfigType>, ConfigType extends GameConfig, T> SubCommandExecutor createConfigSubCommand(
            GamesModule module,
            Class<GameType> gameType,
            Function<ConfigType, Component> queryMessageSupplier,
            BiFunction<CommandSender, String[], Optional<T>> valueCreator, BiConsumer<ConfigType, T> updateValueCallback,
            Function<T, Component> configChangedMessageSupplier
    ) {
        return ConfigCommand.create(
                module.getPlugin(),
                (sender, args) -> {
                    if (!Require.argsAtLeast(sender, args, 1)) return Optional.empty();

                    String worldName = args[0];
                    Optional<GameType> game = requireGame(module, sender, worldName, gameType);
                    if (game.isEmpty()) return Optional.empty();

                    if (game.get().isRunning()) {
                        sender.sendMessage(Component.text("Cannot modify the config of a game while it is running", NamedTextColor.RED));
                        return Optional.empty();
                    }

                    String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);

                    return Optional.of(new ConfigCommand.ContextSupplier.Result<>(game.get().getConfig(), remainingArgs));
                },
                queryMessageSupplier,
                valueCreator,
                updateValueCallback,
                configChangedMessageSupplier,
                module.getGameManager()::saveGameConfig
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBooleanConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Boolean> updateValueCallback,
            Function<Boolean, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 1)) return Optional.empty();
                    return Require.bool(sender, args[0]);
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createIntegerConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Integer> updateValueCallback,
            Function<Integer, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 1)) return Optional.empty();
                    OptionalInt number = Require.integer(sender, args[0]);
                    return number.isPresent() ? Optional.of(number.getAsInt()) : Optional.empty();
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createStringConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, String> updateValueCallback,
            Function<String, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsAtLeast(sender, args, 1)) return Optional.empty();
                    return Optional.of(String.join(" ", args));
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockPositionConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, BlockPosition> updateValueCallback,
            Function<BlockPosition, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 3)) return Optional.empty();
                    return Require.blockPosition(sender, args[0], args[1], args[2]);
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createEntityPositionConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, EntityPosition> updateValueCallback,
            Function<EntityPosition, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 3)) return Optional.empty();
                    return Require.entityPosition(sender, args[0], args[1], args[2]);
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockAreaConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, BlockArea> updateValueCallback,
            Function<BlockArea, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 6)) return Optional.empty();
                    return Require.blockArea(sender, args[0], args[1], args[2], args[3], args[4], args[5]);
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createMaterialConfigSubCommand(
            GamesModule module,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Material> updateValueCallback,
            Function<Material, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommand(
                module,
                gameType,
                queryMessageSupplier,
                (sender, args) -> {
                    if (!Require.argsExact(sender, args, 1)) return Optional.empty();
                    return Require.material(sender, args[0]);
                },
                updateValueCallback,
                configChangedMessageSupplier
        );
    }

    public static <GameType extends Game<GameType, ConfigType>, ConfigType extends GameConfig, ElementType, CollectionType extends Collection<ElementType>> SubCommandExecutor createCollectionConfigSubCommand(
            GamesModule module,
            Class<GameType> gameType,
            Function<ConfigType, CollectionType> collectionSupplier,
            CollectionCommand.ListElementsStrategy<ElementType, CollectionType> listElementsStrategy,
            CollectionCommand.AddElementStrategy<ElementType, CollectionType> addElementStrategy,
            CollectionCommand.RemoveElementStrategy<ElementType, CollectionType> removeElementStrategy
    ) {
        return CollectionCommand.create(
                module.getPlugin(),
                (sender, args) -> {
                    if (!Require.argsAtLeast(sender, args, 1)) return Optional.empty();

                    String worldName = args[0];
                    Optional<GameType> game = requireGame(module, sender, worldName, gameType);
                    if (game.isEmpty()) return Optional.empty();

                    if (game.get().isRunning()) {
                        sender.sendMessage(Component.text("Cannot modify the config of a game while it is running", NamedTextColor.RED));
                        return Optional.empty();
                    }

                    CollectionType collection = collectionSupplier.apply(game.get().getConfig());

                    String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);

                    return Optional.of(new CollectionCommand.CollectionSupplier.Result<>(collection, remainingArgs));
                },
                listElementsStrategy,
                addElementStrategy,
                removeElementStrategy,
                module.getGameManager()::saveGameConfig
        );
    }
}
