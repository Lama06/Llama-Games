package io.github.lama06.lamagames;

import io.github.lama06.lamagames.util.BlockPosition;
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

public abstract class LamaCommand implements TabExecutor {
    protected final LamaGamesPlugin plugin;
    protected final String name;
    private final Map<String, SubCommandExecutor> subCommands = new HashMap<>();

    public LamaCommand(LamaGamesPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        PluginCommand cmd = Bukkit.getPluginCommand(name);
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
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

        subCommands.entrySet().stream().filter(subCommand -> subCommand.getKey().equals(args[0])).findFirst().ifPresent(subCommand ->
                subCommand.getValue().executeSubCommand(sender, Arrays.copyOfRange(args, 1, args.length))
        );

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
            sender.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("This command needs "))
                    .append(Component.text(number))
                    .append(Component.text(number == 1 ? " argument" : " arguments"))
            );
            return true;
        }
        return false;
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

    public static Optional<Game<?, ?>> requireGame(LamaGamesPlugin plugin, CommandSender sender, String worldName) {
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
            LamaGamesPlugin plugin,
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

    public static Optional<Integer> requireNumber(CommandSender sender, String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("This is not a number").color(NamedTextColor.RED));
            return Optional.empty();
        }
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

    public static Optional<BlockPosition> requirePosition(CommandSender sender, String x, String y, String z) {
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

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBlockConfigChangeSubCommand(
            LamaGamesPlugin plugin,
            Class<? extends G> gameType,
            BiConsumer<? super C, ? super BlockPosition> callback,
            String successMessage
    ) {
        return (sender, args) -> {
            if (requireArgsExact(sender, args, 4)) return;

            Optional<G> game = requireGame(plugin, sender, args[0], gameType);
            if (game.isEmpty()) return;

            Optional<BlockPosition> position = requirePosition(sender, args[1], args[2], args[3]);
            if (position.isEmpty()) return;

            callback.accept(game.get().getConfig(), position.get());
            sender.sendMessage(Component.text(successMessage).color(NamedTextColor.GREEN));
        };
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createNumberConfigChangeSubCommand(
            LamaGamesPlugin plugin,
            Class<? extends G> gameType,
            BiConsumer<? super C, ? super Integer> callback,
            String successMessage
    ) {
        return (sender, args) -> {
            if (requireArgsExact(sender, args, 1)) return;

            Optional<G> game = requireGame(plugin, sender, args[0], gameType);
            if (game.isEmpty()) return;

            Optional<Integer> number = requireNumber(sender, args[0]);
            if (number.isEmpty()) return;

            callback.accept(game.get().getConfig(), number.get());
            sender.sendMessage(Component.text(successMessage).color(NamedTextColor.GREEN));
        };
    }

    public static <G extends Game<G, C>, C extends GameConfig> SubCommandExecutor createBooleanConfigChangeSubCommand(
            LamaGamesPlugin plugin,
            Class<? extends G> gameType,
            BiConsumer<? super C, ? super Boolean> callback,
            String successMessage
    ) {
        return (sender, args) -> {
            if (requireArgsExact(sender, args, 1)) return;

            Optional<G> game = requireGame(plugin, sender, args[0], gameType);
            if (game.isEmpty()) return;

            Optional<Boolean> flag = requireBoolean(sender, args[0]);
            if (flag.isEmpty()) return;

            callback.accept(game.get().getConfig(), flag.get());
            sender.sendMessage(Component.text(successMessage).color(NamedTextColor.GREEN));
        };
    }
}
