package io.github.lama06.llamaplugin;

import io.github.lama06.llamaplugin.command.CollectionCommand;
import io.github.lama06.llamaplugin.command.LlamaCommand;
import io.github.lama06.llamaplugin.command.Require;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class LlamaPluginCommand extends LlamaCommand {
    public LlamaPluginCommand(LlamaPlugin plugin) {
        super(plugin, "llamaplugin");

        addSubCommand("enabledModules", CollectionCommand.create(
                plugin,
                (sender, args) -> Optional.of(new CollectionCommand.CollectionSupplier.Result<>(plugin.getModules().getConfig().enabledModules, args)),
                CollectionCommand.listByMappingElements(Component.text("No modules are enabled"), type -> Component.text(type.name())),
                CollectionCommand.forbidElementsWithSameName((sender, args) -> {
                    if (!Require.argsAtLeast(sender, args, 1)) return Optional.empty();

                    String typeName = args[0];
                    Optional<ModuleType<?>> moduleType = ModuleType.byName(typeName);
                    if (moduleType.isEmpty()) {
                        sender.sendMessage(Component.text("No module with this name was found"));
                        return Optional.empty();
                    }

                    return moduleType;
                }, Component.text("Success"), Component.text("Name already exists")),
                CollectionCommand.removeElementsByName(Component.text("Success"), Component.text("Not found")),
                sender -> {}
        ));
    }
}
