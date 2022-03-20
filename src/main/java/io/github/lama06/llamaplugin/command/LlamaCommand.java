package io.github.lama06.llamaplugin.command;

import io.github.lama06.llamaplugin.LlamaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;

import java.util.*;

public abstract class LlamaCommand implements TabExecutor {
    protected final LlamaPlugin plugin;
    protected final String name;
    private final Map<String, SubCommandExecutor> subCommands = new HashMap<>();

    public LlamaCommand(LlamaPlugin plugin, String name) {
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
            subCommand.get().getValue().execute(sender, Arrays.copyOfRange(args, 1, args.length));
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
        void execute(CommandSender sender, String[] args);
    }
}
