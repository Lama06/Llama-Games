package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class WeaponType<T extends AbstractWeapon<T>> {
    private static final Set<WeaponType<?>> TYPES = new HashSet<>();

    public static Set<WeaponType<?>> getTypes() {
        return TYPES;
    }

    private final String displayName;
    private final Material material;
    private final WeaponCreator<T> creator;

    private WeaponType(String displayName, Material material, WeaponCreator<T> creator) {
        this.displayName = displayName;
        this.material = material;
        this.creator = creator;

        TYPES.add(this);
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

    @FunctionalInterface
    public interface WeaponCreator<T extends AbstractWeapon<T>> {
        T createWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type);
    }
}
