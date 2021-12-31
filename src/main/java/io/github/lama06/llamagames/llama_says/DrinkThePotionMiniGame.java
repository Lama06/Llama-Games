package io.github.lama06.llamagames.llama_says;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DrinkThePotionMiniGame extends MiniGame {
    private static final List<PotionType> POTION_TYPES_WITH_EFFECT = Arrays.stream(PotionType.values()).filter(p -> p.getEffectType() != null).collect(Collectors.toList());

    private PotionType potionType;

    public DrinkThePotionMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(), callback);
    }

    @Override
    public void init() {
        potionType = POTION_TYPES_WITH_EFFECT.get(game.getRandom().nextInt(POTION_TYPES_WITH_EFFECT.size()));
        game.getEventCanceler().setCancelItemConsummation(false);
    }

    @Override
    public Component getTitle() {
        ItemStack potion = new ItemStack(Material.POTION);
        potion.editMeta(PotionMeta.class, meta -> meta.setBasePotionData(new PotionData(potionType)));

        return Component.text("Drink the potion: ").append(Component.translatable(potion));
    }

    @Override
    public void handleGameStarted() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(potionType));
        potion.setItemMeta(meta);

        int hotbarSlot = game.getRandom().nextInt(9);

        List<PotionType> possibleWrongPotionTypes = POTION_TYPES_WITH_EFFECT.stream().filter(p -> p != potionType).collect(Collectors.toList());

        for (Player player : game.getPlayers()) {
            for (int i = 0; i <= 8; i++) {
                if (i == hotbarSlot) {
                    player.getInventory().setItem(i, potion);
                    continue;
                }

                PotionType wrongPotionType = possibleWrongPotionTypes.get(game.getRandom().nextInt(possibleWrongPotionTypes.size()));

                ItemStack wrongPotion = new ItemStack(Material.POTION);
                PotionMeta wrongPotionMeta = (PotionMeta) wrongPotion.getItemMeta();
                wrongPotionMeta.setBasePotionData(new PotionData(wrongPotionType));
                wrongPotion.setItemMeta(wrongPotionMeta);

                player.getInventory().setItem(i, wrongPotion);
            }
        }
    }

    @EventHandler
    public void handlePlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        ItemStack item = event.getItem();
        if (item.getType() != Material.POTION) {
            return;
        }

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        PotionType type = meta.getBasePotionData().getType();

        if (type == potionType) {
            result.addSuccessfulPlayer(event.getPlayer());
        } else {
            result.addFailedPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void handleEntityPotionEffectEvent(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player && player.getWorld().equals(game.getWorld())) {
            event.setCancelled(true);
        }
    }
}
