package io.github.lama06.llamaplugin.games.the_floor_is_lava;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.github.lama06.llamaplugin.games.Game;
import io.github.lama06.llamaplugin.games.GameType;
import io.github.lama06.llamaplugin.games.GamesModule;
import io.github.lama06.llamaplugin.util.BlockPosition;
import io.github.lama06.llamaplugin.util.CollectionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class TheFloorIsLavaGame extends Game<TheFloorIsLavaGame, TheFloorIsLavaConfig> {
    private Map<BlockPosition, Integer> blockAges;
    private Map<UUID, Integer> safeWalkUntilTick;

    public TheFloorIsLavaGame(GamesModule module, World world, TheFloorIsLavaConfig config, GameType<TheFloorIsLavaGame, TheFloorIsLavaConfig> type) {
        super(module, world, config, type);
    }

    @Override
    public void handleGameStarted(String[] args) {
        resetFloors();

        blockAges = new HashMap<>();
        for (Floor floor : config.floors) {
            for (BlockPosition block : floor.blocks.getBlocks()) {
                blockAges.put(block, 0);
            }
        }

        safeWalkUntilTick = new HashMap<>();
    }

    @Override
    public void handleGameEnded(GameEndReason reason) {
        resetFloors();

        for (Player player : getPlayers()) {
            cleanupPlayer(player);
        }

        for (Item item : world.getEntitiesByClass(Item.class)) {
            item.remove();
        }

        blockAges = null;
        safeWalkUntilTick = null;
    }

    @Override
    public boolean canStart(int numberOfPlayers) {
        return super.canStart(numberOfPlayers) && numberOfPlayers >= 1;
    }

    @Override
    public boolean canContinueAfterNumberOfPlayersChanged(int numberOfPlayers) {
        return numberOfPlayers >= 1;
    }

    @Override
    public void handlePlayerLeft(Player player) {
        cleanupPlayer(player);
    }

    private void cleanupPlayer(Player player) {
        player.getInventory().clear();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    private void resetFloors() {
        for (Floor floor : config.floors) {
            floor.blocks.fill(world, config.blockStates.get(0));
        }
    }

    @EventHandler
    public void ageAndUpdateBlock(ServerTickEndEvent event) {
        if (!running) {
            return;
        }

        for (Player player : getPlayers()) {
            Integer safeWalkUntil = safeWalkUntilTick.get(player.getUniqueId());
            if (safeWalkUntil != null && safeWalkUntil >= Bukkit.getCurrentTick()) {
                continue;
            }

            Block block = player.getLocation().subtract(0, 1, 0).getBlock();
            BlockData blockState = block.getBlockData();
            BlockPosition blockPosition = new BlockPosition(block);
            if (!isFloorBlock(blockPosition)) {
                continue;
            }


            int newAge = blockAges.get(blockPosition) + 1;
            blockAges.put(blockPosition, newAge);

            int newBlockTypeIndex = newAge / config.blockAgeTime;
            if (newBlockTypeIndex >= config.blockStates.size()) {
                block.setType(Material.AIR);
                continue;
            }
            BlockData newBlockState = config.blockStates.get(newBlockTypeIndex);

            if (!blockState.equals(newBlockState)) {
                block.setBlockData(newBlockState);
            }
        }
    }

    private boolean isFloorBlock(BlockPosition position) {
        return config.floors.stream().anyMatch(floor -> floor.blocks.containsBlock(position));
    }

    @EventHandler
    private void killPlayersThatTouchDeadlyBlocks(PlayerMoveEvent event) {
        if (!running || !getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getTo().clone().subtract(0, 1, 0).getBlock().getType() != config.deadlyBlock) {
            return;
        }

        setSpectator(event.getPlayer(), true);
        cleanupPlayer(event.getPlayer());
    }

    private static final String PERK_ITEM_ID_KEY = "perk_item_id";

    @EventHandler
    public void spawnPerkItems(ServerTickStartEvent event) {
        if (!running || random.nextInt(100) != 0) {
            return;
        }

        List<BlockPosition> possibleSpawnLocations = config.floors.stream()
                .map(floor -> floor.blocks.getBlocks())
                .flatMap(Collection::stream)
                .toList();
        BlockPosition spawnLocation = CollectionUtil.pickRandomElement(possibleSpawnLocations).add(0, 1, 0);

        PerkType perk = CollectionUtil.pickRandomElement(PerkType.values(), random);

        ItemStack itemStack = perk.createItem(module);
        world.spawn(spawnLocation.asLocation(world), Item.class, item -> {
            item.setItemStack(itemStack);

            item.getPersistentDataContainer().set(new NamespacedKey(module.getPlugin(), PERK_ITEM_ID_KEY), PersistentDataType.INTEGER, perk.ordinal());
        });
    }

    @EventHandler
    public void handlePlayerPickupPerk(EntityPickupItemEvent event) {
        if (!running || !(event.getEntity() instanceof Player player) || !getPlayers().contains(player)) {
            return;
        }

        Item item = event.getItem();

        Integer id = item.getPersistentDataContainer().get(new NamespacedKey(module.getPlugin(), PERK_ITEM_ID_KEY), PersistentDataType.INTEGER);
        if (id == null) {
            return;
        }
        PerkType perk = PerkType.values()[id];

        player.getInventory().addItem(perk.createItem(module));

        item.remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerUsePerk(PlayerInteractEvent event) {
        if (!running || !getPlayers().contains(event.getPlayer())) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        Integer id = meta.getPersistentDataContainer().get(new NamespacedKey(module.getPlugin(), PERK_ITEM_ID_KEY), PersistentDataType.INTEGER);
        if (id == null) {
            return;
        }
        PerkType perk = PerkType.values()[id];

        perk.onUse(this, event.getPlayer());

        event.getPlayer().getInventory().setItemInMainHand(null);
    }

    private enum PerkType {
        JUMP(Material.FEATHER, Component.text("Jump Boost")) {
            @Override
            public void onUse(TheFloorIsLavaGame game, Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3*20, 1));
            }
        },

        LEVITATION(Material.ELYTRA, Component.text("Levitation")) {
            @Override
            public void onUse(TheFloorIsLavaGame game, Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5*20, 1));
            }
        },

        SAFE_WALK(Material.LEATHER_BOOTS, Component.text("Safe Walk")) {
            @Override
            public void onUse(TheFloorIsLavaGame game, Player player) {
                game.safeWalkUntilTick.put(player.getUniqueId(), Bukkit.getCurrentTick() + 7*20);
                player.sendActionBar(Component.text("Blocks will stop disappearing under your feet", NamedTextColor.BLUE));
            }
        };

        public final Material material;
        public final Component displayName;

        PerkType(Material material, Component displayName) {
            this.material = material;
            this.displayName = displayName;
        }

        public abstract void onUse(TheFloorIsLavaGame game, Player player);

        public ItemStack createItem(GamesModule plugin) {
            ItemStack item = new ItemStack(material);
            item.editMeta(meta -> {
                meta.displayName(displayName);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin.getPlugin(), PERK_ITEM_ID_KEY), PersistentDataType.INTEGER, ordinal());
            });
            return item;
        }
    }
}
