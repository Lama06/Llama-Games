package io.github.lama06.lamagames;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class LamaCommand implements TabExecutor {
    protected final LamaGamesPlugin plugin;
    protected final String name;
    private final Map<String, SubCommandExecutor> subCommands = new HashMap<>();

    public LamaCommand(LamaGamesPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        Bukkit.getPluginCommand(name).setExecutor(this);
        Bukkit.getPluginCommand(name).setTabCompleter(this);
    }

    public void addSubCommand(String name, SubCommandExecutor executor) {
        subCommands.put(name, executor);
    }

    private BaseComponent[] getHelpMessage() {
        ComponentBuilder text = new ComponentBuilder();
        for (Map.Entry<String, SubCommandExecutor> subCommand : subCommands.entrySet()) {
            text.append("/").append(name).append(" ").append(subCommand.getKey()).append("\n");
        }
        return text.create();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length == 0) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sender.spigot().sendMessage(getHelpMessage());
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
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("This commands needs " + number + " arguments").create());
            return true;
        }
        return false;
    }

    public static Optional<Player> requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("This command can only be used by players").create());
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<Player> requireOnlinePlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("No player with this name was found on the server").create());
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<World> requireWorld(CommandSender sender, String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("No world with this name was found").create());
            return Optional.empty();
        }
        return Optional.of(world);
    }

    public static Optional<Game<?, ?>> requireGame(LamaGamesPlugin plugin, CommandSender sender, String worldName) {
        Optional<World> world = requireWorld(sender, worldName);
        if (world.isEmpty()) return Optional.empty();

        Optional<Game<?, ?>> game = plugin.getGameManager().getGameForWorld(world.get());
        if (game.isEmpty()) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("No game exists in this world").create());
            return Optional.empty();
        }
        return game;
    }
}
