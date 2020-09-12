package de.flojo.jam.game.player;

public enum PlayerId {
    ONE, TWO;

    public <T> T ifOne(T a , T b) {
        return this == ONE ? a : b;
    }

    public <T> T ifTwo(T a , T b) {
        return this == TWO ? a : b;
    }

}
