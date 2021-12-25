package io.github.lama06.llamagames;

import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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

    public static Optional<NamespacedKey> requireNamespacedKey(CommandSender sender, String name) {
        NamespacedKey key = NamespacedKey.fromString(name);
        if (key == null) {
            sender.sendMessage("%s is not a valid key".formatted(name));
            return Optional.empty();
        }

        return Optional.of(key);
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

    public static Optional<BlockArea> requireBlockArea(CommandSender sender, String x1, String y1, String z1, String x2, String y2, String z2) {
        Optional<BlockPosition> position1 = requireBlockPosition(sender, x1, y1, z1);
        if (position1.isEmpty()) return Optional.empty();

        Optional<BlockPosition> position2 = requireBlockPosition(sender, x2, y2, z2);
        if (position2.isEmpty()) return Optional.empty();

        return Optional.of(new BlockArea(position1.get(), position2.get()));
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

    public static Optional<Material> requireMaterial(CommandSender sender, String name) {
        Optional<NamespacedKey> key = requireNamespacedKey(sender, name);
        if (key.isEmpty()) return Optional.empty();

        Material material = Registry.MATERIAL.get(key.get());
        if (material == null) {
            sender.sendMessage(Component.text("There is no block or item named %s".formatted(key.get())));
            return Optional.empty();
        }

        return Optional.of(material);
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
            Class<G> gameType
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
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, T> configChangedCallback,
            Function<T, Component> configChangedMessageSupplier,
            BiFunction<CommandSender, String[], Optional<T>> configChangeCommandLogic
    ) {
        return (sender, args) -> {
            if (requireArgsAtLeast(sender, args, 1)) return;

            Optional<G> game = requireGame(plugin, sender, args[0], gameType);
            if (game.isEmpty()) return;

            if (args.length == 1) {
                sender.sendMessage(queryMessageSupplier.apply(game.get().getConfig()));
            } else {
                if (game.get().isRunning()) {
                    sender.sendMessage(Component.text("Cannot change the config of a game that is currently running", NamedTextColor.RED));
                    return;
                }

                Optional<T> newConfigValue = configChangeCommandLogic.apply(sender, Arrays.copyOfRange(args, 1, args.length));
                if (newConfigValue.isEmpty()) {
                    return;
                }

                try {
                    plugin.getGameManager().saveGameConfig();
                } catch (GameManager.GamesSaveFailedException e) {
                    sender.sendMessage(Component.text("Failed to save the game config", NamedTextColor.RED));
                    return;
                }

                configChangedCallback.accept(game.get().getConfig(), newConfigValue.get());
                sender.sendMessage(configChangedMessageSupplier.apply(newConfigValue.get()).color(NamedTextColor.GREEN));
            }
        };
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBooleanConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Boolean> configChangedCallback,
            Function<Boolean, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (!requireArgsExact(sender, args, 2)) return Optional.empty();
                    return requireBoolean(sender, args[1]);
                }
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createIntegerConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Integer> configChangedCallback,
            Function<Integer, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (!requireArgsExact(sender, args, 1)) return Optional.empty();
                    return requireInteger(sender, args[0]);
                }
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockPositionConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, BlockPosition> configChangedCallback,
            Function<BlockPosition, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (!requireArgsExact(sender, args, 3)) return Optional.empty();
                    return requireBlockPosition(sender, args[0], args[1], args[2]);
                }
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createEntityPositionSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, EntityPosition> configChangedCallback,
            Function<EntityPosition, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (!requireArgsExact(sender, args, 3)) return Optional.empty();
                    return requireEntityPosition(sender, args[0], args[1], args[2]);
                }
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockAreaConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, BlockArea> configChangedCallback,
            Function<BlockArea, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 6)) return Optional.empty();
                    return requireBlockArea(sender, args[0], args[1], args[2], args[3], args[4], args[5]);
                }
        );
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createMaterialConfigSubCommand(
            LlamaGamesPlugin plugin,
            Class<G> gameType,
            Function<C, Component> queryMessageSupplier,
            BiConsumer<C, Material> configChangedCallback,
            Function<Material, Component> configChangedMessageSupplier
    ) {
        return createConfigSubCommandBootstrap(
                plugin,
                gameType,
                queryMessageSupplier,
                configChangedCallback,
                configChangedMessageSupplier,
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 1)) return Optional.empty();
                    return requireMaterial(sender, args[0]);
                }
        );
    }
}
