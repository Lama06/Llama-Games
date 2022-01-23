package io.github.lama06.llamagames.zombies.weapon;

import io.github.lama06.llamagames.zombies.ZombiesGame;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unused")
public record WeaponSystemType<T extends WeaponSystem>(Function<ZombiesGame, T> creator) {
    private static final Set<WeaponSystemType<?>> TYPES = new HashSet<>();

    public static Set<WeaponSystemType<?>> getTypes() {
        return TYPES;
    }

    public static final WeaponSystemType<AmmoComponent.ReloadAmmoSystem> RELOAD_AMMO = new WeaponSystemType<>(AmmoComponent.ReloadAmmoSystem::new);

    public static final WeaponSystemType<AttackCooldownComponent.TickAttackCooldownSystem> TICK_ATTACK_COOLDOWN = new WeaponSystemType<>(
            AttackCooldownComponent.TickAttackCooldownSystem::new
    );

    public static final WeaponSystemType<MeleeComponent.MeleeAttackSystem> MELEE_ATTACK = new WeaponSystemType<>(MeleeComponent.MeleeAttackSystem::new);

    public static final WeaponSystemType<ShootComponent.ShootSystem> SHOOT = new WeaponSystemType<>(ShootComponent.ShootSystem::new);

    public static final WeaponSystemType<AmmoComponent.DisplayTotalAmmoAsLevelSystem> DISPLAY_TOTAL_AMMO = new WeaponSystemType<>(
            AmmoComponent.DisplayTotalAmmoAsLevelSystem::new
    );

    public static final WeaponSystemType<AmmoComponent.DisplayMagazineAmmoAsXpSystem> DISPLAY_MAGAZINE_AMMO = new WeaponSystemType<>(
            AmmoComponent.DisplayMagazineAmmoAsXpSystem::new
    );

    public WeaponSystemType {
        TYPES.add(this);
    }
}
