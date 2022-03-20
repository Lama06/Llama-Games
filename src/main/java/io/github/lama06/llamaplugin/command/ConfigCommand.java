package io.github.lama06.llamaplugin.command;

import io.github.lama06.llamaplugin.LlamaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.function.*;

public final class ConfigCommand {
    private ConfigCommand() { }

    public static <ContextType, T> LlamaCommand.SubCommandExecutor create(
            LlamaPlugin plugin,
            ContextSupplier<ContextType> contextSupplier,
            Function<ContextType, Component> queryMessageSupplier,
            BiFunction<CommandSender, String[], Optional<T>> valueCreator,
            BiConsumer<ContextType, T> updateValueCallback,
            Function<T, Component> configChangedMessageSupplier,
            Consumer<CommandSender> runAfterOperation
    ) {
        return (sender, args) -> {
            if (!Require.op(sender)) return;

            Optional<ContextSupplier.Result<ContextType>> contextResult = contextSupplier.getContext(sender, args);
            if (contextResult.isEmpty()) return;

            ContextType context = contextResult.get().context;
            String[] remainingArgs = contextResult.get().remainingArgs;

            if (remainingArgs.length == 0) {
                sender.sendMessage(queryMessageSupplier.apply(context));
            } else {
                Optional<T> newValue = valueCreator.apply(sender, remainingArgs);
                if (newValue.isEmpty()) return;

                updateValueCallback.accept(context, newValue.get());

                sender.sendMessage(configChangedMessageSupplier.apply(newValue.get()).color(NamedTextColor.GREEN));
            }

            runAfterOperation.accept(sender);
        };
    }

    @FunctionalInterface
    public interface ContextSupplier<ContextType> {
        record Result<ContextType>(ContextType context, String[] remainingArgs) { }

        Optional<Result<ContextType>> getContext(CommandSender sender, String[] args);
    }
}
