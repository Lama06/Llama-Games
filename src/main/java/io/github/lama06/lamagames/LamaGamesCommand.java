package io.github.lama06.lamagames;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LamaGamesCommand extends LamaCommand {
    public LamaGamesCommand(LamaGamesPlugin plugin, String name) {
        super(plugin, name);
        addSubCommand("create", this::create);
        addSubCommand("delete", this::delete);
        addSubCommand("list", this::list);
        addSubCommand("start", this::start);
        addSubCommand("stop", this::stop);
    }

    public void create(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 2)) return;

        Optional<World> world = requireWorld(sender, args[0]);
        if (world.isEmpty()) return;

        Optional<GameType<?, ?>> type = GameType.getByName(args[1]);
        if (type.isEmpty()) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("Invalid game type specified").create());
            return;
        }

        if (plugin.getGameManager().getGameForWorld(world.get()).isPresent()) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("A game already exists in this world").create());
            return;
        }

        plugin.getGameManager().createGame(world.get(), type.get());
        sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.GREEN).append("The game was successfully created").create());

        if (sender instanceof Player player && !player.getWorld().equals(world.get())) {
            player.teleport(world.get().getSpawnLocation());
        }
    }

    public void delete(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<Game<?, ?>> game = requireGame(plugin, sender, args[0]);
        if (game.isEmpty()) return;

        try {
            plugin.getGameManager().deleteGame(game.get().getWorld());
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.GREEN).append("The game was successfully deleted").create());
        } catch (GameManager.GamesSaveFailedException e) {
            sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.RED).append("Internal error while saving the config file").create());
        }
    }

    public void list(CommandSender sender, String[] args) {
        if (plugin.getGameManager().getGames().isEmpty()) {
            sender.sendMessage("There are no games on the server");
            return;
        }

        ComponentBuilder text = new ComponentBuilder().append("Games on the server:");
        for (Game<?, ?> game : plugin.getGameManager().getGames()) {
            text.append("\n").append(game.getWorld().getName()).append(" -> ").append(game.getType().getName());
        }
        sender.spigot().sendMessage(text.create());
    }

    public void start(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<Game<?, ?>> game = requireGame(plugin, sender, args[0]);
        if (game.isEmpty()) return;

        game.get().startGame();
        sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.GREEN).append("The game was successfully started").create());
    }

    public void stop(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<Game<?, ?>> game = requireGame(plugin, sender, args[0]);
        if (game.isEmpty()) return;

        game.get().endGame();
        sender.spigot().sendMessage(new ComponentBuilder().color(ChatColor.GREEN).append("The game was successfully stopped").create());
    }
}
