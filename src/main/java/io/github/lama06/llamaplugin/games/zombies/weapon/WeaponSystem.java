package io.github.lama06.llamaplugin.games.zombies.weapon;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.games.zombies.ZombiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class WeaponSystem implements Listener {
    protected final ZombiesGame game;

    public WeaponSystem(ZombiesGame game) {
        this.game = game;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, game.getModule().getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    protected Set<Weapon<?>> getAllWeapons() {
        Set<Weapon<?>> weapons = new HashSet<>();

        for (ZombiesPlayer player : game.getZombiesPlayers()) {
            for (Weapon<?> weapon : player.getWeapons()) {
                if (weapon != null) weapons.add(weapon);
            }
        }

        return weapons;
    }
}
