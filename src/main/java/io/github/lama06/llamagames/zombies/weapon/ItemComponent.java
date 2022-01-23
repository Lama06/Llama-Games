package io.github.lama06.llamagames.zombies.weapon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import io.github.lama06.llamagames.zombies.ZombiesPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class ItemComponent {
    public ItemStack item;

    public ItemComponent(ItemStack item) {
        this.item = item;
    }

    public static class DisplayItemsSystem extends WeaponSystem {
        public DisplayItemsSystem(ZombiesGame game) {
            super(game);
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (ZombiesPlayer player : game.getZombiesPlayers()) {
                List<Weapon<?>> weapons = player.getWeapons();
                for (int i = 0; i < weapons.size(); i++) {
                    Weapon<?> weapon = weapons.get(i);
                    if (weapon == null) {
                        player.getPlayer().getInventory().setItem(i, null);
                        continue;
                    }

                    ItemComponent component = weapon.getComponents().getComponent(ItemComponent.class);
                    if (component == null) {
                        player.getPlayer().getInventory().setItem(i, null);
                        continue;
                    }

                    ItemStack item = new ItemStack(component.item);
                    item.editMeta(meta -> meta.displayName(Component.text(weapon.getType().getDisplayName())));

                    if (!Objects.equals(player.getPlayer().getInventory().getItem(i), item)) {
                        player.getPlayer().getInventory().setItem(i, item);
                    }
                }
            }
        }
    }
}
