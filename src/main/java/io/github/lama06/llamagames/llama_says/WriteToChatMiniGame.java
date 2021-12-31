package io.github.lama06.llamagames.llama_says;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.function.Consumer;

public class WriteToChatMiniGame extends MiniGame {
    private static final List<String> WORDS = List.of(
            "Gouverneur",
            "President",
            "Idiot",
            "Hello",
            "No",
            "Infrastructure",
            "Axolotl",
            "Goat"
    );

    private String word;

    public WriteToChatMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback) {
        super(game, new RankedResult(), callback);
    }

    @Override
    public void init() {
        word = WORDS.get(game.getRandom().nextInt(WORDS.size()));
    }

    @Override
    public Component getTitle() {
        return Component.text("Write to chat: " + word);
    }

    @EventHandler
    public void handlePlayerChatEvent(AsyncChatEvent event) {
        Bukkit.getScheduler().runTask(game.getPlugin(), () -> {
            if (PaperComponents.plainSerializer().serialize(event.message()).trim().equalsIgnoreCase(word)) {
                result.addSuccessfulPlayer(event.getPlayer());
            }
        });
    }
}
