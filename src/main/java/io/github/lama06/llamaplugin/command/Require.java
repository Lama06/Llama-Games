package io.github.lama06.llamaplugin.command;

import io.github.lama06.llamaplugin.util.BlockArea;
import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.EntityPosition;
import io.github.lama06.llamaplugin.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class Require {
    private Require() { }

    public static boolean argsExact(CommandSender sender, String[] args, int number) {
        if (args.length != number) {
            sender.sendMessage(Component.text("This number of arguments that were given to this command is not correct").color(NamedTextColor.RED));
            return false;
        }
        return true;
    }
    public static boolean argsAtLeast(CommandSender sender, String[] args, int number) {
        if (args.length < number) {
            sender.sendMessage(Component.text("This command needs more arguments").color(NamedTextColor.RED));
            return false;
        }
        return true;
    }

    @SafeVarargs
    public static boolean args(CommandSender sender, String[] args, Pair<Integer, Runnable>... cases) {
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

    public static Optional<Boolean> bool(CommandSender sender, String text) {
        if (!TRUE_STRINGS.contains(text) && !FALSE_STRINGS.contains(text)) {
            sender.sendMessage(Component.text("Failed to parse: %s".formatted(text)).color(NamedTextColor.RED));
            return Optional.empty();
        }

        return Optional.of(TRUE_STRINGS.contains(text));
    }

    public static OptionalInt integer(CommandSender sender, String text) {
        try {
            return OptionalInt.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("This is not a number").color(NamedTextColor.RED));
            return OptionalInt.empty();
        }
    }

    public static Optional<NamespacedKey> namespacedKey(CommandSender sender, String name) {
        NamespacedKey key = NamespacedKey.fromString(name);
        if (key == null) {
            sender.sendMessage("%s is not a valid key".formatted(name));
            return Optional.empty();
        }

        return Optional.of(key);
    }

    public static Optional<BlockPosition> blockPosition(CommandSender sender, String x, String y, String z) {
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

    public static Optional<BlockArea> blockArea(CommandSender sender, String x1, String y1, String z1, String x2, String y2, String z2) {
        Optional<BlockPosition> position1 = blockPosition(sender, x1, y1, z1);
        if (position1.isEmpty()) return Optional.empty();

        Optional<BlockPosition> position2 = blockPosition(sender, x2, y2, z2);
        if (position2.isEmpty()) return Optional.empty();

        return Optional.of(new BlockArea(position1.get(), position2.get()));
    }

    public static Optional<EntityPosition> entityPosition(CommandSender sender, String x, String y, String z) {
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

    public static Optional<Material> material(CommandSender sender, String name) {
        Optional<NamespacedKey> key = namespacedKey(sender, name);
        if (key.isEmpty()) return Optional.empty();

        Material material = Registry.MATERIAL.get(key.get());
        if (material == null) {
            sender.sendMessage(Component.text("There is no block or item named %s".formatted(key.get())));
            return Optional.empty();
        }

        return Optional.of(material);
    }

    public static Optional<BlockData> blockData(CommandSender sender, String text) {
        try {
            return Optional.of(Bukkit.createBlockData(text));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static final Component NO_PERMISSION_MSG = Component.text("You don't have the permission to execute this command", NamedTextColor.RED);

    public static boolean op(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        } else if (sender instanceof Player player) {
            if (!player.isOp()) {
                player.sendMessage(NO_PERMISSION_MSG);
            }

            return player.isOp();
        } else {
            sender.sendMessage(NO_PERMISSION_MSG);
            return false;
        }
    }

    public static Optional<Player> player(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<Player> onlinePlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.sendMessage(Component.text("No player with this name was found on the server").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(player);
    }

    public static Optional<World> world(CommandSender sender, String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            sender.sendMessage(Component.text("No world with this name was found").color(NamedTextColor.RED));
            return Optional.empty();
        }
        return Optional.of(world);
    }
}
