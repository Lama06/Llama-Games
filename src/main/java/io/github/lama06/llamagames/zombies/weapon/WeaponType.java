package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import io.github.lama06.llamagames.zombies.ZombiesGame;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class WeaponType<T extends Weapon<T>> {
    private static final Set<WeaponType<?>> TYPES = new HashSet<>();

    public Set<WeaponType<?>> getTypes() {
        return TYPES;
    }

    public static Optional<WeaponType<?>> getByName(String name) {
        return TYPES.stream().filter(type -> type.name.equals(name)).findFirst();
    }

    public static final WeaponType<Knife> KNIFE = Knife.TYPE;

    public static final WeaponType<Rifle> RIFLE = Rifle.TYPE;

    private final String name;
    private final String displayName;
    private final WeaponCreator<T> creator;

    public WeaponType(String name, String displayName, WeaponCreator<T> creator) {
        this.name = name;
        this.displayName = displayName;
        this.creator = creator;

        TYPES.add(this);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public WeaponCreator<T> getCreator() {
        return creator;
    }

    public interface WeaponCreator<T extends Weapon<T>> {
        T createWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type);
    }
}
