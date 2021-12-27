package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class WeaponType<T extends AbstractWeapon<T>> {
    private static final Set<WeaponType<?>> TYPES = new HashSet<>();

    public static Set<WeaponType<?>> getTypes() {
        return TYPES;
    }

    public static Optional<WeaponType<?>> byName(String name) {
        return TYPES.stream().filter(type -> type.getName().equals(name)).findFirst();
    }

    public static final WeaponType<Knife> KNIFE = new WeaponType<>(
            "knife",
            "Knife",
            Material.STONE_SWORD,
            Knife::new
    );

    public static final WeaponType<Rifle> RIFLE = new WeaponType<>(
            "rifle",
            "Rifle",
            Material.STONE_HOE,
            Rifle::new
    );

    public static final WeaponType<Shotgun> SHOTGUN = new WeaponType<>(
            "shotgun",
            "Shotgun",
            Material.WOODEN_HOE,
            Shotgun::new
    );

    private final String name;
    private final String displayName;
    private final Material material;
    private final WeaponCreator<T> creator;

    private WeaponType(String name, String displayName, Material material, WeaponCreator<T> creator) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.creator = creator;

        TYPES.add(this);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public WeaponCreator<T> getCreator() {
        return creator;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @FunctionalInterface
    public interface WeaponCreator<T extends AbstractWeapon<T>> {
        T createWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type);
    }
}
