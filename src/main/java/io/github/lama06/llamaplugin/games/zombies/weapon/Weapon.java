package io.github.lama06.llamaplugin.games.zombies.weapon;

import io.github.lama06.llamaplugin.games.zombies.ZombiesGame;
import io.github.lama06.llamaplugin.games.zombies.ZombiesPlayer;
import io.github.lama06.llamaplugin.util.ComponentContainer;

public abstract class Weapon<T extends Weapon<T>> {
    protected final ZombiesGame game;
    protected final ZombiesPlayer player;
    protected final WeaponType<T> type;
    protected final ComponentContainer components = new ComponentContainer();

    public Weapon(ZombiesGame game, ZombiesPlayer player, WeaponType<T> type) {
        this.game = game;
        this.player = player;
        this.type = type;

        initComponents();
    }

    public abstract void initComponents();

    public ZombiesGame getGame() {
        return game;
    }

    public ComponentContainer getComponents() {
        return components;
    }

    public WeaponType<T> getType() {
        return type;
    }
}
