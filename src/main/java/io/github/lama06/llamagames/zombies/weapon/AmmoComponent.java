package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class AmmoComponent {
    public int maxTotalAmmo;
    public int totalAmmoRemaining;
    public int maxMagazineAmmo;
    public int magazineAmmoRemaining;
    public int reloadTime;
    public int remainingReloadTime = 0;

    public AmmoComponent(int maxTotalAmmo, int maxMagazineAmmo, int reloadTime) {
        this.maxTotalAmmo = maxTotalAmmo;
        this.maxMagazineAmmo = maxMagazineAmmo;
        this.reloadTime = reloadTime;

        totalAmmoRemaining = maxTotalAmmo-maxMagazineAmmo;
        magazineAmmoRemaining = maxMagazineAmmo;
    }

    public static class ReloadAmmoSystem extends WeaponSystem {
        public ReloadAmmoSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (Weapon<?> weapon : getAllWeapons()) {
                AmmoComponent component = weapon.getComponents().getComponent(AmmoComponent.class);
                if (component == null) {
                    continue;
                }

                if (component.remainingReloadTime > 1) {
                    component.remainingReloadTime--;
                    continue;
                }

                if (component.remainingReloadTime == 1) {
                    component.remainingReloadTime = 0;

                    int reloadAmmo = Math.min(component.maxMagazineAmmo, component.totalAmmoRemaining);
                    component.totalAmmoRemaining -= reloadAmmo;
                    component.magazineAmmoRemaining = reloadAmmo;

                    continue;
                }

                if (component.remainingReloadTime == 0 && component.magazineAmmoRemaining == 0 && component.totalAmmoRemaining > 0) {
                    component.remainingReloadTime = component.reloadTime;
                }
            }
        }

        @EventHandler
        public void handlePlayerInteractEvent(PlayerInteractEvent event) {
            if (!event.getAction().isRightClick()) {
                return;
            }

            ZombiesPlayer player = game.getZombiesPlayer(event.getPlayer());
            if (player == null) {
                return;
            }

            Weapon<?> weapon = player.getWeaponInHand();
            if (weapon == null) {
                return;
            }

            AmmoComponent component = weapon.getComponents().getComponent(AmmoComponent.class);
            if (component == null) {
                return;
            }

            if (component.totalAmmoRemaining == 0 || component.magazineAmmoRemaining == component.maxMagazineAmmo) {
                return;
            }

            component.remainingReloadTime = component.reloadTime;
        }
    }

    public static class DisplayTotalAmmoAsLevelSystem extends WeaponSystem {
        public DisplayTotalAmmoAsLevelSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (ZombiesPlayer player : game.getZombiesPlayers()) {
                if (player == null) {
                    continue;
                }

                Weapon<?> weapon = player.getWeaponInHand();
                if (weapon == null) {
                    player.getPlayer().setLevel(0);
                    continue;
                }

                AmmoComponent component = weapon.getComponents().getComponent(AmmoComponent.class);
                if (component == null) {
                    player.getPlayer().setLevel(0);
                    continue;
                }

                if (player.getPlayer().getLevel() != component.totalAmmoRemaining) {
                    player.getPlayer().setLevel(component.totalAmmoRemaining);
                }
            }
        }
    }

    public static class DisplayMagazineAmmoAsXpSystem extends WeaponSystem {
        public DisplayMagazineAmmoAsXpSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (ZombiesPlayer player : game.getZombiesPlayers()) {
                Weapon<?> weapon = player.getWeaponInHand();
                if (weapon == null) {
                    player.getPlayer().setExp(0);
                    continue;
                }

                AmmoComponent component = weapon.getComponents().getComponent(AmmoComponent.class);
                if (component == null) {
                    player.getPlayer().setExp(0);
                    continue;
                }

                float xp = (float) component.magazineAmmoRemaining / (float) component.maxMagazineAmmo;
                if (player.getPlayer().getExp() != xp) {
                    player.getPlayer().setExp(xp);
                }
            }
        }
    }
}
