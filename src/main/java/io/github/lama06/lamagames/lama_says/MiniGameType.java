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
