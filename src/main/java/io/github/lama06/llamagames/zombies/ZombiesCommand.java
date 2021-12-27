package io.github.lama06.llamagames.zombies;

import io.github.lama06.llamagames.GameCommand;
import io.github.lama06.llamagames.LlamaGamesPlugin;
import io.github.lama06.llamagames.util.BlockArea;
import io.github.lama06.llamagames.util.BlockPosition;
import io.github.lama06.llamagames.util.EntityPosition;
import io.github.lama06.llamagames.zombies.weapon.WeaponShop;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class ZombiesCommand extends GameCommand {
    public ZombiesCommand(LlamaGamesPlugin plugin) {
        super(plugin, "zombies");

        addSubCommand("startArea", createStringConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> Component.text("The start area is currently set to %s".formatted(config.startArea)),
                (config, name) -> config.startArea = name,
                name -> Component.text("The start area is now set to %s".formatted(name))
        ));

        addSubCommand("windows", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.windows,
                Component.text("There are no windows"),
                window -> Component.text("%s: %s (Zombie spawn location: %s, Area: %s)".formatted(window.name, window.windowBlocks, window.zombieSpawnLocation, window.area)),
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 11)) return Optional.empty();

                    String name = args[0];
                    String area = args[1];

                    Optional<BlockArea> windowBlocks = requireBlockArea(sender, args[2], args[3], args[4], args[5], args[6], args[7]);
                    if (windowBlocks.isEmpty()) return Optional.empty();

                    Optional<EntityPosition> spawnPosition = requireEntityPosition(sender, args[8], args[9], args[10]);
                    if (spawnPosition.isEmpty()) return Optional.empty();

                    return Optional.of(new Window(name, area, windowBlocks.get(), spawnPosition.get()));
                },
                Component.text("A window with this name already exists"),
                Component.text("The window was successfully created"),
                (sender, args, window) -> {
                    if (requireArgsExact(sender, args, 1)) return Optional.empty();
                    String name = args[0];
                    return Optional.of(window.name.equals(name));
                },
                Component.text("The window was successfully removed")
        ));

        addSubCommand("doors", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.doors,
                Component.text("There are no doors :("),
                door -> Component.text("%s: %s (Price: %d, Activation block: %s, Areas: %s %s, Template: %s)".formatted(
                        door.name,
                        door.location,
                        door.price,
                        door.activationBlock,
                        door.area1,
                        door.area2,
                        door.templateLocation
                )),
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 19)) return Optional.empty();

                    String name = args[0];
                    String area1 = args[1];
                    String area2 = args[2];

                    Optional<Integer> price = requireInteger(sender, args[3]);
                    if (price.isEmpty()) return Optional.empty();

                    Optional<BlockArea> location = requireBlockArea(sender, args[4], args[5], args[6], args[7], args[8], args[9]);
                    if (location.isEmpty()) return Optional.empty();

                    Optional<BlockArea> template = requireBlockArea(sender, args[10], args[11], args[12], args[13], args[14], args[15]);
                    if (template.isEmpty()) return Optional.empty();

                    Optional<BlockPosition> activationBlock = requireBlockPosition(sender, args[16], args[17], args[18]);
                    if (activationBlock.isEmpty()) return Optional.empty();

                    return Optional.of(new Door(name, area1, area2, price.get(), location.get(), template.get(), activationBlock.get()));
                },
                Component.text("A door with this name already exists"),
                Component.text("The door was successfully created"),
                (sender, args, door) -> {
                    if (requireArgsExact(sender, args, 1)) return Optional.empty();
                    String name = args[0];
                    return Optional.of(door.name.equals(name));
                },
                Component.text("The door was successfully removed")
        ));

        addSubCommand("additionalZombieSpawnLocations", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.additionalSpawnLocations,
                Component.text("There are no additional zombie spawn locations"),
                location -> Component.text("%s: %s".formatted(location.name, location.position)),
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 5)) return Optional.empty();

                    String name = args[0];
                    String area = args[1];

                    Optional<EntityPosition> position = requireEntityPosition(sender, args[2], args[3], args[4]);
                    if (position.isEmpty()) return Optional.empty();

                    return Optional.of(new ZombieSpawnLocation(name, area, position.get()));
                },
                Component.text("A zombie spawn location with this name already exists"),
                Component.text("The spawn location was successfully created"),
                (sender, args, door) -> {
                    if (requireArgsExact(sender, args, 1)) return Optional.empty();
                    String name = args[0];
                    return Optional.of(door.name.equals(name));
                },
                Component.text("The zombie spawn location was successfully removed")
        ));

        addSubCommand("weaponShops", createCollectionConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> config.weaponShops,
                Component.text("There are no weapon shops"),
                shop -> Component.text("%s: %s (Price: %d, Refill price: %d, Weapon: %s)".formatted(shop.name, shop.activationBlock, shop.price, shop.refillPrice, shop.weapon)),
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 7)) return Optional.empty();

                    String name = args[0];

                    Optional<BlockPosition> activationBlock = requireBlockPosition(sender, args[1], args[2], args[3]);
                    if (activationBlock.isEmpty()) return Optional.empty();

                    Optional<Integer> price = requireInteger(sender, args[4]);
                    if (price.isEmpty()) return Optional.empty();

                    Optional<Integer> refillPrice = requireInteger(sender, args[5]);
                    if (refillPrice.isEmpty()) return Optional.empty();

                    Optional<WeaponType<?>> weaponType = WeaponType.byName(args[6]);
                    if (weaponType.isEmpty()) {
                        sender.sendMessage(Component.text("Could not find a weapon named like this", NamedTextColor.RED));
                        return Optional.empty();
                    }

                    return Optional.of(new WeaponShop(name, activationBlock.get(), price.get(), refillPrice.get(), weaponType.get()));
                },
                Component.text("A weapon shop with this name already exists"),
                Component.text("The weapon shop was successfully created"),
                (sender, args, shop) -> {
                    if (requireArgsExact(sender, args, 1)) return Optional.empty();
                    String name = args[0];
                    return Optional.of(shop.name.equals(name));
                },
                Component.text("The weapon shop was successfully removed")
        ));

        addSubCommand("powerSwitch", createConfigSubCommand(
                plugin,
                ZombiesGame.class,
                config -> {
                    if (config.powerSwitch == null) {
                        return Component.text("The power switch has not been configured yet");
                    }

                    return Component.text("The power switch is currently located at %s and costs %d gold".formatted(
                            config.powerSwitch.position,
                            config.powerSwitch.price
                    ));
                },
                (config, powerSwitch) -> config.powerSwitch = powerSwitch,
                powerSwitch -> Component.text("The power switch is now located at %s and costs %d gold".formatted(powerSwitch.position, powerSwitch.price)),
                (sender, args) -> {
                    if (requireArgsExact(sender, args, 4)) return Optional.empty();

                    Optional<BlockPosition> position = requireBlockPosition(sender, args[0], args[1], args[2]);
                    if (position.isEmpty()) return Optional.empty();

                    Optional<Integer> price = requireInteger(sender, args[3]);
                    if (price.isEmpty()) return Optional.empty();

                    return Optional.of(new PowerSwitch(position.get(), price.get()));
                }
        ));
    }
}
