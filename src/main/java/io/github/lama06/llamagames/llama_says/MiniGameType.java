package io.github.lama06.llamagames.llama_says;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class MiniGameType<T extends MiniGame> {
    private static final List<MiniGameType<?>> types = new ArrayList<>();

    public static List<MiniGameType<?>> getTypes() {
        return types;
    }

    public static Optional<MiniGameType<?>> byName(String name) {
        return types.stream().filter(type -> type.getName().equals(name)).findFirst();
    }

    public static final MiniGameType<DrinkThePotionMiniGame> DRINK_THE_POTION = new MiniGameType<>(
            "drink_the_potion",
            DrinkThePotionMiniGame::new
    );

    public static final MiniGameType<SlapOtherPlayerMiniGame> SLAP_OTHER_PLAYER = new MiniGameType<>(
            "slap_other_player",
            SlapOtherPlayerMiniGame::new
    );

    public static final MiniGameType<SmeltMiniGame> SMELT = new MiniGameType<>(
            "smelt",
            SmeltMiniGame::new
    );

    public static final MiniGameType<GetIntoMinecartMiniGame> GET_INTO_MINECART = new MiniGameType<>(
            "get_into_minecart",
            GetIntoMinecartMiniGame::new
    );

    public static final MiniGameType<ThrowTridentMiniGame> THROW_TRIDENT_AT_OTHER_PLAYER = new MiniGameType<>(
            "throw_trident",
            ThrowTridentMiniGame::new
    );

    public static final MiniGameType<InsertMusicDiscMiniGame> INSERT_MUSIC_DISC = new MiniGameType<>(
            "insert_music_disc",
            InsertMusicDiscMiniGame::new
    );

    public static final MiniGameType<WriteToChatMiniGame> WRITE_TO_CHAT = new MiniGameType<>(
            "write_to_chat",
            WriteToChatMiniGame::new
    );

    public static final MiniGameType<StandStillMiniGame> STAND_STILL = new MiniGameType<>(
            "stand_still",
            StandStillMiniGame::new
    );

    public static final MiniGameType<EatUntilYouAreFullMiniGame> EAT_UNTIL_YOU_ARE_FULL = new MiniGameType<>(
            "eat_until_you_are_full",
            EatUntilYouAreFullMiniGame::new
    );

    public static final MiniGameType<ShootYourselfWithAnArrowMiniGame> SHOOT_YOURSELF_WITH_AN_ARROW = new MiniGameType<>(
            "shoot_yourself_with_an_arrow",
            ShootYourselfWithAnArrowMiniGame::new
    );

    public static final MiniGameType<BuildIronGolemMiniGame> BUIlD_IRON_GOLEM = new MiniGameType<>(
            "build_iron_golem",
            BuildIronGolemMiniGame::new
    );

    public static final MiniGameType<FeedAnimalMiniGame> FEED_ANIMAL = new MiniGameType<>(
            "feed_animal",
            FeedAnimalMiniGame::new
    );

    public static final MiniGameType<PutOnArmorStandMiniGame> PUT_ON_ARMOR_STAND = new MiniGameType<>(
            "put_on_armor_stand",
            PutOnArmorStandMiniGame::new
    );

    public static final MiniGameType<RemoveFromInventoryMiniGame> REMOVE_FROM_INVENTORY = new MiniGameType<>(
            "remove_from_inventory",
            RemoveFromInventoryMiniGame::new
    );

    public static final MiniGameType<DoNotGetKilledByTntMiniGame> DO_NOT_GET_KILLED_BY_TNT = new MiniGameType<>(
            "avoid_tnt",
            DoNotGetKilledByTntMiniGame::new
    );

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
    public interface MiniGameCreator<T extends MiniGame> {
        T createMiniGame(LlamaSaysGame game, Consumer<MiniGame> callback);
    }
}
