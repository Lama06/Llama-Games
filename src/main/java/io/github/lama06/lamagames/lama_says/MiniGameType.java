package io.github.lama06.lamagames.lama_says;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class MiniGameType<T extends MiniGame<T>> {
    private static final List<MiniGameType<?>> types = new ArrayList<>();

    public static List<MiniGameType<?>> getTypes() {
        return types;
    }

    public static final MiniGameType<DrinkThePotionMiniGame> DRINK_THE_POTION = new MiniGameType<>("drink_the_potion", DrinkThePotionMiniGame::new);
    public static final MiniGameType<SlapOtherPlayerMiniGame> SLAP_OTHER_PLAYER = new MiniGameType<>("slap_other_player", SlapOtherPlayerMiniGame::new);
    public static final MiniGameType<SmeltMiniGame> SMELT = new MiniGameType<>("smelt", SmeltMiniGame::new);
    public static final MiniGameType<GetIntoMinecartMiniGame> GET_INTO_MINECART = new MiniGameType<>("get_into_minecart", GetIntoMinecartMiniGame::new);
    public static final MiniGameType<ThrowTridentMiniGame> THROW_TRIDENT_AT_OTHER_PLAYER = new MiniGameType<>("throw_trident", ThrowTridentMiniGame::new);
    public static final MiniGameType<InsertMusicDiscMiniGame> INSERT_MUSIC_DISC = new MiniGameType<>("insert_music_disc", InsertMusicDiscMiniGame::new);
    public static final MiniGameType<WriteToChatMiniGame> WRITE_TO_CHAT = new MiniGameType<>("write_to_chat", WriteToChatMiniGame::new);
    public static final MiniGameType<StandStillMiniGame> STAND_STILL = new MiniGameType<>("stand_still", StandStillMiniGame::new);

    private final String name;
    private final MiniGameCreator<T> creator;

    private MiniGameType(String name, MiniGameCreator<T> creator) {
        this.name = name;
        this.creator = creator;

        types.add(this);
    }

    public String getName() {
        return name;
    }

    public MiniGameCreator<T> getCreator() {
        return creator;
    }

    @FunctionalInterface
    public interface MiniGameCreator<T extends MiniGame<T>> {
        T createMiniGame(LamaSaysGame game, Consumer<T> callback);
    }
}
