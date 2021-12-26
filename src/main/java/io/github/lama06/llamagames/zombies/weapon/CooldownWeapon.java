package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class CooldownWeapon<T extends CooldownWeapon<T>> extends AbstractWeapon<T> {
    protected int cooldownRemaining = 0;

    public CooldownWeapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type) {
        super(game, player, type);
    }

    public abstract int getCooldownAfterUse();

    public boolean hasCooldown() {
        return cooldownRemaining != 0;
    }

    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    @EventHandler
    private void tickCooldownTimer(ServerTickStartEvent event) {
        if (cooldownRemaining != 0) {
            cooldownRemaining--;
        }
    }

    @Override
    public boolean canUse() {
        return cooldownRemaining == 0;
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        cooldownRemaining = getCooldownAfterUse();
    }
}
