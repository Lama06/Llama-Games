package io.github.lama06.llamagames.zombies.monster;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamagames.zombies.ZombiesGame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EquipmentComponent {
    public ItemStack helmet;
    public ItemStack chestplate;
    public ItemStack leggins;
    public ItemStack boots;
    public ItemStack hand;

    public EquipmentComponent(ItemStack helmet, ItemStack chestplate, ItemStack leggins, ItemStack boots, ItemStack hand) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggins = leggins;
        this.boots = boots;
        this.hand = hand;
    }

    public static class SyncEquipmentSystem extends MonsterSystem {
        public SyncEquipmentSystem(ZombiesGame game) {
            super(game);
        }

        private static void syncEquipment(EntityEquipment equipment, EquipmentSlot slot, ItemStack item) {
            ItemStack currentItem = equipment.getItem(slot);
            if (currentItem != item) {
                equipment.setItem(slot, item, true);
            }
        }

        @EventHandler
        public void tick(ServerTickStartEvent event) {
            if (!game.isRunning()) return;

            for (Monster<?, ?> monster : game.getMonsters()) {
                EquipmentComponent component = monster.getComponents().getComponent(EquipmentComponent.class);
                if (component == null) {
                    continue;
                }

                if (!(monster.getEntity() instanceof LivingEntity entity)) {
                    continue;
                }
                EntityEquipment equipment = entity.getEquipment();
                if (equipment == null) {
                    continue;
                }

                syncEquipment(equipment, EquipmentSlot.HEAD, component.helmet);
                syncEquipment(equipment, EquipmentSlot.CHEST, component.chestplate);
                syncEquipment(equipment, EquipmentSlot.LEGS, component.leggins);
                syncEquipment(equipment, EquipmentSlot.FEET, component.boots);
                syncEquipment(equipment, EquipmentSlot.HAND, component.hand);
            }
        }
    }
}
