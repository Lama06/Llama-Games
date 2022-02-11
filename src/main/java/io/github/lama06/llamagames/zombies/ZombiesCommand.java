package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.OptionalInt;

public class ZombiesCommand extends GameCommand {
    public ZombiesCommand(LlamaGamesPlugin plugin) {
        super(plugin, "zombies");

        addSubCommand("startArea", createStringConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> Component.text("The start area is currently named %s".formatted(config.startArea)),
                (config, name) -> config.startArea = name,
                name -> Component.text("The start area is now named %s".formatted(name))
        ));

        addSubCommand("doors", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.doors,
                new MapElementsListStrategy<>(
                        Component.text("There are no doors"),
                        door -> Component.text("%s - Location: %s, Template %s, Areas: %s %s, Gold: %d"
                                .formatted(door.name, door.blocks, door.template, door.area1, door.area1, door.gold))),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 16)) return Optional.empty();

                    String name = args[0];

                    Optional<BlockArea> location = requireBlockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (location.isEmpty()) return Optional.empty();

                    Optional<BlockArea> template = requireBlockArea(sender, args[7], args[8], args[9], args[10], args[11], args[12]);
                    if (template.isEmpty()) return Optional.empty();

                    Optional<BlockPosition> activationBlock = requireBlockPosition(sender, args[13], args[14], args[15]);
                    if (activationBlock.isEmpty()) return Optional.empty();

                    String area1 = args[13];
                    String area2 = args[14];

                    OptionalInt gold = requireInteger(sender, args[15]);
                    if (gold.isEmpty()) return Optional.empty();

                    return Optional.of(new Door(name, area1, area2, activationBlock.get(), gold.getAsInt(), location.get(), template.get()));
                }, Component.text("The door was successfully added"), Component.text("A door with this name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("The door was successfully removed"), Component.text("No door with this name was found"))
        ));

        addSubCommand("windows", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.windows,
                new MapElementsListStrategy<>(
                        Component.text("There are no windows"),
                        window -> Component.text("%s - Location: %s, Spawn Location: %s, Area: %s"
                                .formatted(window.name, window.blocks, window.spawnLocation, window.area))
                ),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 11)) return Optional.empty();

                    String name = args[0];

                    Optional<BlockArea> location = requireBlockArea(sender, args[1], args[2], args[3], args[4], args[5], args[6]);
                    if (location.isEmpty()) return Optional.empty();

                    Optional<EntityPosition> spawnLocation = requireEntityPosition(sender, args[7], args[8], args[9]);
                    if (spawnLocation.isEmpty()) return Optional.empty();

                    String area = args[10];

                    return Optional.of(new Window(name, area, spawnLocation.get(), location.get()));
                }, Component.text("The window was successfully added"), Component.text("A window with this name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("The window was successfully removed"), Component.text("No window with this name exists"))
        ));

        addSubCommand("weaponShops", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.weaponShops,
                new MapElementsListStrategy<>(
                        Component.text("There are no weapon shops"),
                        shop -> Component.text("%s - Weapon: %s, Location: %s, Price: %d"
                                .formatted(shop.name, shop.weapon.getName(), shop.activationBLock, shop.gold))
                ),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 7)) return Optional.empty();

                    String name = args[0];

                    Optional<WeaponType<?>> weaponType = WeaponType.getByName(args[1]);
                    if (weaponType.isEmpty()) return Optional.empty();

                    Optional<BlockPosition> activationBlock = requireBlockPosition(sender, args[2], args[3], args[4]);
                    if (activationBlock.isEmpty()) return Optional.empty();

                    OptionalInt gold = requireInteger(sender, args[5]);
                    if (gold.isEmpty()) return Optional.empty();

                    OptionalInt refillPrice = requireInteger(sender, args[6]);
                    if (refillPrice.isEmpty()) return Optional.empty();

                    return Optional.of(new WeaponShop(name, weaponType.get(), activationBlock.get(), gold.getAsInt(), refillPrice.getAsInt()));
                }, Component.text("The weapon shop was successfully added"), Component.text("A weapon shop with this name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("The weapon shop was successfully removed"), Component.text("No weapon shop with this name was found"))
        ));

        addSubCommand("additionalZombieSpawnLocations", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.additionalZombieSpawnLocations,
                new MapElementsListStrategy<>(
                        Component.text("There are no extra zombie spawn locations"),
                        spawnLocation -> Component.text("%s - Location: %s, Area: %s"
                                .formatted(spawnLocation.name, spawnLocation.position, spawnLocation.area))
                ),
                new ForbidElementsWithSameNameAddStrategy<>((sender, args) -> {
                    if (!requireArgsExact(sender, args, 5)) return Optional.empty();

                    String name = args[0];

                    Optional<EntityPosition> location = requireEntityPosition(sender, args[1], args[2], args[3]);
                    if (location.isEmpty()) return Optional.empty();

                    String area = args[4];

                    return Optional.of(new AdditionalZombieSpawnLocation(name, area, location.get()));
                }, Component.text("The zombie spawn location was successfully added"), Component.text("A zombie spawn location with this name already exists")),
                new RemoveElementByNameStrategy<>(Component.text("The zombie spawn location was successfully removed"), Component.text("No zombie spawn location with this name was found"))
        ));

        addSubCommand("powerSwitch", createConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> Component.text("The power switch is currently located at %s and costs %d"
                        .formatted(config.powerSwitch.activationBlock, config.powerSwitch.gold)),
                (config, powerSwitch) -> config.powerSwitch = powerSwitch,
                powerSwitch -> Component.text("The power switch is now at %s and costs %d"
                        .formatted(powerSwitch.activationBlock, powerSwitch.gold)),
                (sender, args) -> {
                    if (!requireArgsExact(sender, args, 4)) return Optional.empty();

                    Optional<BlockPosition> activationBlock = requireBlockPosition(sender, args[0], args[1], args[2]);
                    if (activationBlock.isEmpty()) return Optional.empty();

                    OptionalInt gold = requireInteger(sender, args[3]);
                    if (gold.isEmpty()) return Optional.empty();

                    return Optional.of(new PowerSwitch(gold.getAsInt(), activationBlock.get()));
                }
        ));

        addSubCommand("spawnRates", (sender, args) ->
                sender.sendMessage(Component.text("Spawn Rates can currently only be modified directly through the config file")));
    }
}
