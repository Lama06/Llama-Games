package io.github.lama06.llamagames.llama_says;

import com.google.common.collect.ImmutableMap;
import io.github.lama06.llamagames.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InsertMusicDiscMiniGame extends MiniGame {
    private static final Map<Material, String> MUSIC_DISCS = ImmutableMap.<Material, String>builder()
            .put(Material.MUSIC_DISC_PIGSTEP, "Pigstep")
            .put(Material.MUSIC_DISC_11, "Never gonna give you up")
            .put(Material.MUSIC_DISC_CAT, "Beautiful Times")
            .put(Material.MUSIC_DISC_13, "Lucid Dream")
            .put(Material.MUSIC_DISC_BLOCKS, "Good Time")
            .put(Material.MUSIC_DISC_CHIRP, "Cinematic")
            .put(Material.MUSIC_DISC_FAR, "Rick Astley Best Of")
            .put(Material.MUSIC_DISC_WARD, "100 Best Rick Astley Songs")
            .put(Material.MUSIC_DISC_WAIT, "Never gonna give you up remix")
            .put(Material.MUSIC_DISC_STRAD, "Rick Astley Compilation")
            .build();

    private List<Material> musicDiscs;
    private Material disc;

    public InsertMusicDiscMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(), callback);
    }

    @Override
    public void init() {
        musicDiscs = Util.pickRandomElements(MUSIC_DISCS.keySet(), 9, game.getRandom());
        disc = musicDiscs.get(game.getRandom().nextInt(musicDiscs.size()));
    }

    @Override
    public Component getTitle() {
        return Component.text("Insert \"").append(Component.text(MUSIC_DISCS.get(disc))).append(Component.text("\" into the jukebox"));
    }

    @Override
    public void handleGameStarted() {
        setJukeboxBlock(Material.JUKEBOX.createBlockData());

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < 9; i++) {
                Material type = musicDiscs.get(i);
                ItemStack item = new ItemStack(type);
                item.editMeta(meta -> meta.displayName(Component.text(MUSIC_DISCS.get(type))));
                player.getInventory().setItem(i, item);
            }
        }
    }

    @Override
    public void handleGameEnded() {
        setJukeboxBlock(Material.AIR.createBlockData());
    }

    private void setJukeboxBlock(BlockData state) {
        game.getWorld().setBlockData(game.getConfig().getFloorCenter().asLocation(game.getWorld()).add(0, 1, 0), state);
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (!game.getPlayers().contains(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.JUKEBOX) {
            return;
        }

        event.setCancelled(true);

        if (event.getItem() != null && event.getItem().getType() != disc) {
            result.addFailedPlayer(event.getPlayer());
        } else {
            result.addSuccessfulPlayer(event.getPlayer());
        }
    }
}
