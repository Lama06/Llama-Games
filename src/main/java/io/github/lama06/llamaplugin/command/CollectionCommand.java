package io.github.lama06.llamaplugin.command;

import io.github.lama06.llamaplugin.LlamaPlugin;
import io.github.lama06.llamaplugin.util.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public final class CollectionCommand {
    private CollectionCommand() { }

    public static <ElementType, CollectionType extends Collection<ElementType>> LlamaCommand.SubCommandExecutor create(
            LlamaPlugin plugin,
            CollectionSupplier<ElementType, CollectionType> collectionSupplier,
            ListElementsStrategy<ElementType, CollectionType> listElementsStrategy,
            AddElementStrategy<ElementType, CollectionType> addElementStrategy,
            RemoveElementStrategy<ElementType, CollectionType> removeElementStrategy,
            Consumer<CommandSender> runAfterOperation
    ) {
        return (sender, args) -> {
            if (!Require.op(sender)) return;

            if (!Require.argsAtLeast(sender, args, 1)) return;

            Optional<CollectionSupplier.Result<CollectionType>> context = collectionSupplier.getCollection(sender, Arrays.copyOfRange(args, 1, args.length));
            if (context.isEmpty()) return;

            CollectionType collection = context.get().collection;
            String[] remainingArgs = context.get().remainingArgs;

            switch (args[0]) {
                case "list" -> listElementsStrategy.handleList(plugin, sender, remainingArgs, collection);
                case "add" -> addElementStrategy.handleAdd(plugin, sender, remainingArgs, collection);
                case "remove" -> removeElementStrategy.handleRemove(plugin, sender, remainingArgs, collection);
                default -> sender.sendMessage(Component.text("No sub command with this name was found. Please use: list/add/remove", NamedTextColor.RED));
            }

            runAfterOperation.accept(sender);
        };
    }

    @FunctionalInterface
    public interface CollectionSupplier<ElementType, CollectionType extends Collection<ElementType>> {
        record Result<CollectionType>(CollectionType collection, String[] remainingArgs) { }

        Optional<Result<CollectionType>> getCollection(CommandSender sender, String[] args);
    }

    @FunctionalInterface
    public interface ListElementsStrategy<ElementType, CollectionType extends Collection<ElementType>> {
        void handleList(LlamaPlugin plugin, CommandSender sender, String[] args, CollectionType collection);
    }

    public static <ElementType, CollectionType extends Collection<ElementType>> ListElementsStrategy<ElementType, CollectionType> listByMappingElements(
            Component noElementsMessage,
            Function<ElementType, Component> mapper
    ) {
        return (plugin, sender, args, collection) -> {
            if (collection.isEmpty()) {
                sender.sendMessage(noElementsMessage.color(NamedTextColor.RED));
                return;
            }

            TextComponent.Builder builder = Component.text();
            boolean first = true;
            for (ElementType element : collection) {
                if (first) {
                    first = false;
                } else {
                    builder.append(Component.newline());
                }
                builder.append(mapper.apply(element));
            }

            sender.sendMessage(builder);
        };
    }

    @FunctionalInterface
    public interface ElementCreator<ElementType> {
        Optional<ElementType> createCollectionElement(CommandSender sender, String[] args);
    }

    @FunctionalInterface
    public interface AddElementStrategy<ElementType, CollectionType extends Collection<ElementType>> {
        void handleAdd(LlamaPlugin plugin, CommandSender sender, String[] args, CollectionType collection);
    }

    public static <ElementType, CollectionType extends List<ElementType>> AddElementStrategy<ElementType, CollectionType> addToList(
            ElementCreator<ElementType> creator,
            Component successMessage
    ) {
        return (plugin, sender, args, list) -> {
            Optional<ElementType> element = creator.createCollectionElement(sender, args);
            if (element.isEmpty()) return;

            list.add(element.get());

            sender.sendMessage(successMessage.color(NamedTextColor.GREEN));
        };
    }

    public static <ElementType, CollectionType extends Collection<ElementType>> AddElementStrategy<ElementType, CollectionType> forbidDuplicates(
            ElementCreator<ElementType> creator,
            BiPredicate<ElementType, ElementType> duplicateChecker,
            Component successMessage,
            Component alreadyExistsMessage
    ) {
        return (plugin, sender, args, collection) -> {
            Optional<ElementType> newElement = creator.createCollectionElement(sender, args);
            if (newElement.isEmpty()) return;

            boolean duplicate = false;
            for (ElementType element : collection) {
                if (duplicateChecker.test(element, newElement.get())) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                sender.sendMessage(alreadyExistsMessage.color(NamedTextColor.RED));
                return;
            }

            collection.add(newElement.get());
            sender.sendMessage(successMessage);
        };
    }

    public static <ElementType extends Named, CollectionType extends Collection<ElementType>> AddElementStrategy<ElementType, CollectionType> forbidElementsWithSameName(
            ElementCreator<ElementType> creator,
            Component successMessage,
            Component nameAlreadyExistsMessage
    ) {
        return forbidDuplicates(
                creator,
                (element1, element2) -> element1.getName().equals(element2.getName()),
                successMessage,
                nameAlreadyExistsMessage
        );
    }

    @FunctionalInterface
    public interface RemoveElementStrategy<ElementType, CollectionType extends Collection<ElementType>> {
        void handleRemove(LlamaPlugin plugin, CommandSender sender, String[] args, CollectionType collection);
    }

    public static <ElementType, CollectionType extends List<ElementType>> RemoveElementStrategy<ElementType, CollectionType> removeByListIndex(
            Component removedMessage
    ) {
        return (plugin, sender, args, list) -> {
            if (!Require.argsExact(sender, args, 1)) return;

            OptionalInt index = Require.integer(sender, args[0]);
            if (index.isEmpty()) return;

            if (index.getAsInt() < 0 || index.getAsInt() >= list.size()) {
                sender.sendMessage(Component.text("Index out of bounds", NamedTextColor.RED));
                return;
            }

            list.remove(index.getAsInt());
            sender.sendMessage(removedMessage.color(NamedTextColor.GREEN));
        };
    }

    public static <ElementType extends Named, CollectionType extends Collection<ElementType>> RemoveElementStrategy<ElementType, CollectionType> removeElementsByName(
            Component removedMessage,
            Component notFoundMessage
    ) {
        return (plugin, sender, args, collection) -> {
            if (!Require.argsExact(sender, args, 1)) return;

            String name = args[0];

            for (Iterator<ElementType> iterator = collection.iterator(); iterator.hasNext();) {
                ElementType element = iterator.next();

                if (element.getName().equals(name)) {
                    iterator.remove();
                    sender.sendMessage(removedMessage.color(NamedTextColor.GREEN));
                    return;
                }
            }

            sender.sendMessage(notFoundMessage.color(NamedTextColor.RED));
        };
    }
}
