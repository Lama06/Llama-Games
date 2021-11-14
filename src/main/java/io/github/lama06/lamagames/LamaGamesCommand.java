package io.github.lama06.lamagames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(Component.text("Invalid game type specified").color(NamedTextColor.RED));
            return;
        }

        if (plugin.getGameManager().getGameForWorld(world.get()).isPresent()) {
            sender.sendMessage(Component.text("A game already exists in this world").color(NamedTextColor.RED));
            return;
        }

        plugin.getGameManager().createGame(world.get(), type.get());
        sender.sendMessage(Component.text("The game was successfully created").color(NamedTextColor.GREEN));

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
            sender.sendMessage(Component.text("The game was successfully deleted").color(NamedTextColor.GREEN));
        } catch (GameManager.GamesSaveFailedException e) {
            sender.sendMessage(Component.text("Internal error while saving the config file").color(NamedTextColor.RED));
        }
    }

    public void list(CommandSender sender, String[] args) {
        if (plugin.getGameManager().getGames().isEmpty()) {
            sender.sendMessage("There are no games on the server");
            return;
        }

        TextComponent.Builder text = Component.text().content("Games on the server:");
        for (Game<?, ?> game : plugin.getGameManager().getGames()) {
            text.append(Component.newline());
            text.append(Component.text(game.getWorld().getName()));
            text.append(Component.text(" -> "));
            text.append(Component.text(game.getType().getName()));
        }
        sender.sendMessage(text);
    }

    public void start(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<Game<?, ?>> game = requireGame(plugin, sender, args[0]);
        if (game.isEmpty()) return;

        game.get().startGame();
        sender.sendMessage(Component.text("The game was successfully started").color(NamedTextColor.GREEN));
    }

    public void stop(CommandSender sender, String[] args) {
        if (requireArgsExact(sender, args, 1)) return;

        Optional<Game<?, ?>> game = requireGame(plugin, sender, args[0]);
        if (game.isEmpty()) return;

        game.get().endGame();
        sender.sendMessage(Component.text("The game was successfully stopped").color(NamedTextColor.GREEN));
    }
}
