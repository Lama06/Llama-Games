package io.github.lama06.llamagames;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class LlamaCommand implements TabExecutor {
    protected final LlamaGamesPlugin plugin;
    protected final String name;
    private final Map<String, SubCommandExecutor> subCommands = new HashMap<>();

    public LlamaCommand(LlamaGamesPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        PluginCommand cmd = Bukkit.getPluginCommand(name);
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        } else {
            plugin.getLogger().warning("Failed to register command: %s".formatted(name));
        }
    }

    public void addSubCommand(String name, SubCommandExecutor executor) {
        subCommands.put(name, executor);
    }

    private Component getHelpMessage() {
        TextComponent.Builder builder = Component.text();
        boolean first = true;
        for (Map.Entry<String, SubCommandExecutor> subCommand : subCommands.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(Component.newline());
            }
            builder.append(Component.text("/"));
            builder.append(Component.text(name));
            builder.append(Component.text(" "));
            builder.append(Component.text(subCommand.getKey()));
        }
        return builder.build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length == 0) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sender.sendMessage(getHelpMessage());

            return true;
        }

        Optional<Map.Entry<String, SubCommandExecutor>> subCommand = subCommands.entrySet().stream().filter(s -> s.getKey().equals(args[0])).findFirst();
        if (subCommand.isPresent()) {
            subCommand.get().getValue().executeSubCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage(Component.text("There is not sub command with this name", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(subCommands.keySet());
            completions.add("help");
            return completions;
        }

        return Collections.emptyList();
    }

    @FunctionalInterface
    public interface SubCommandExecutor {
        void executeSubCommand(CommandSender sender, String[] args);
    }

    public static boolean requireArgsExact(CommandSender sender, String[] args, int number) {
        if (args.length != number) {
            sender.sendMessage(Component.text("This command needs %d args".formatted(number)).color(NamedTextColor.RED));
            return true;
        }
        return false;
    }

    public static boolean requireArgsAtLeast(CommandSender sender, String[] args, int number) {
        if (args.length < number) {
            sender.sendMessage(Component.text("This command needs at least %d args".formatted(number)).color(NamedTextColor.RED));
            return true;
        }
        return false;
    }

    @SafeVarargs
    public static boolean requireArgs(CommandSender sender, String[] args, Pair<Integer, Runnable>... cases) {
        Optional<Pair<Integer, Runnable>> argsCase = Arrays.stream(cases).filter(pair -> pair.getLeft() == args.length).findAny();
        if (argsCase.isPresent()) {
            argsCase.get().getRight().run();
            return true;
        }

        sender.sendMessage(Component.text("The number of arguments is not correct").color(NamedTextColor.RED));
        return false;
    }

    private static final Set<String> TRUE_STRINGS = Set.of("yes", "true", "on", "1");
    private static final Set<String> FALSE_STRINGS = Set.of("no", "false", "off", "0");

    public static Optional<Boolean> requireBoolean(CommandSender sender, String text) {
        if (!TRUE_STRINGS.contains(text) && !FALSE_STRINGS.contains(text)) {
            sender.sendMessage(Component.text("Failed to parse: %s".formatted(text)).color(NamedTextColor.RED));
            return Optional.empty();
        }

        return Optional.of(TRUE_STRINGS.contains(text));
    }

    public static Optional<Integer> requireInteger(CommandSender sender, String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("This is not a number").color(NamedTextColor.RED));
            return Optional.empty();
        }
    }

    public static Optional<BlockPosition> requireBlockPosition(CommandSender sender, String x, String y, String z) {
        try {
            int xPos = Integer.parseInt(x);
            int yPos = Integer.parseInt(y);
            int zPos = Integer.parseInt(z);

            return Optional.of(new BlockPosition(xPos, yPos, zPos));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("This is not a number").color(NamedTextColor.RED));
            return Optional.empty();
        }
    }

    public static Optional<EntityPosition> requireEntityPosition(CommandSender sender, String x, String y, String z) {
        try {
            double xPos = Integer.parseInt(x);
            double yPos = Integer.parseInt(y);
            double zPos = Integer.parseInt(z);

            return Optional.of(new EntityPosition(xPos, yPos, zPos));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("This is not a number").color(NamedTextColor.RED));
            return Optional.empty();
        }
    }

    public static Optional<Player> requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<Player> requireOnlinePlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.sendMessage(Component.text("No player with this name was found on the server").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<World> requireWorld(CommandSender sender, String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            sender.sendMessage(Component.text("No world with this name was found").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(world);
    }

    public static Optional<Game<?, ?>> requireGame(LlamaGamesPlugin plugin, CommandSender sender, String worldName) {
        Optional<World> world = requireWorld(sender, worldName);
        if (world.isEmpty()) return Optional.empty();

        Optional<Game<?, ?>> game = plugin.getGameManager().getGameForWorld(world.get());
        if (game.isEmpty()) {
            sender.sendMessage(Component.text("No game exists in this world").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return game;
    }

    @SuppressWarnings("unchecked")
    public static <G extends Game<G, ?>> Optional<G> requireGame(
            LlamaGamesPlugin plugin,
            CommandSender sender,
            String worldName,
            Class<? extends G> gameType
    ) {
        Optional<Game<?, ?>> game = requireGame(plugin, sender, worldName);
        if (game.isEmpty()) return Optional.empty();

        if (gameType != null && !gameType.isAssignableFrom(game.get().getClass())) {
            sender.sendMessage(Component.text("This world contains a game that is not of the right type").color(NamedTextColor.RED));
            return Optional.empty();
        }

        return Optional.of((G) game.get());
    }

    public static <G extends Game<G, C>, C extends GameConfig, T> SubCommandExecutor createConfigSubCommandBootstrap(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super T> configChangedCallback,
            Function<? super T, String> configChangedMessageSupplier,
            Function<? super G, ? extends T> configChangeCommandLogic
    ) {
        return (sender, args) -> {
            if (requireArgsAtLeast(sender, args, 1)) return;

            Optional<G> game = requireGame(plugin, sender, args[0], gameType);
            if (game.isEmpty()) return;

            boolean querySubCommandExecuted = requireArgs(
                    sender,
                    args,
                    Pair.of(1, () -> sender.sendMessage(Component.text(queryMessageSupplier.apply(game.get().getConfig()))))
            );

            if (!querySubCommandExecuted) {
                T newConfigValue = configChangeCommandLogic.apply(game.get());
                if (newConfigValue == null) {
                    return;
                }

                try {
                    plugin.getGameManager().saveGameConfig();
                } catch (GameManager.GamesSaveFailedException e) {
                    sender.sendMessage(Component.text("Failed to save the game config", NamedTextColor.RED));
                    return;
                }

                configChangedCallback.accept(game.get().getConfig(), newConfigValue);
                sender.sendMessage(Component.text(configChangedMessageSupplier.apply(newConfigValue), NamedTextColor.GREEN));
            }
        };
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBooleanConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super Boolean> configChangedCallback,
            Function<? super Boolean, String> configChangedMessageSupplier
    ) {
        return (sender, args) -> createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                game -> {
                    if (!requireArgsExact(sender, args, 2)) return null;
                    return requireBoolean(sender, args[1]).orElse(null);
                }
        ).executeSubCommand(sender, args);
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createIntegerConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super Integer> configChangedCallback,
            Function<? super Integer, String> configChangedMessageSupplier
    ) {
        return (sender, args) -> createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                game -> {
                    if (!requireArgsExact(sender, args, 2)) return null;
                    return requireInteger(sender, args[1]).orElse(null);
                }
        ).executeSubCommand(sender, args);
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockPositionConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super BlockPosition> configChangedCallback,
            Function<? super BlockPosition, String> configChangedMessageSupplier
    ) {
        return (sender, args) -> createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                game -> {
                    if (!requireArgsExact(sender, args, 4)) return null;
                    return requireBlockPosition(sender, args[1], args[2], args[3]).orElse(null);
                }
        ).executeSubCommand(sender, args);
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createEntityPositionSubCommand(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super EntityPosition> configChangedCallback,
            Function<? super EntityPosition, String> configChangedMessageSupplier
    ) {
        return (sender, args) -> createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                game -> {
                    if (!requireArgsExact(sender, args, 4)) return null;
                    return requireEntityPosition(sender, args[1], args[2], args[3]).orElse(null);
                }
        ).executeSubCommand(sender, args);
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockAreaConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<? extends G> gameType,
            Function<? super C, String> queryMessageSupplier,
            BiConsumer<? super C, ? super BlockArea> configChangedCallback,
            Function<? super BlockArea, String> configChangedMessageSupplier
    ) {
        return (sender, args) -> createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                game -> {
                    if (requireArgsExact(sender, args, 7)) return null;

                    Optional<BlockPosition> position1 = requireBlockPosition(sender, args[1], args[2], args[3]);
                    if (position1.isEmpty()) return null;

                    Optional<BlockPosition> position2 = requireBlockPosition(sender, args[4], args[5], args[6]);
                    if (position2.isEmpty()) return null;

                    return new BlockArea(position1.get(), position2.get());
                }
        ).executeSubCommand(sender, args);
    }
}
