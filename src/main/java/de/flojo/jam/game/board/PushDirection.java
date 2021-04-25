package de.flojo.jam.game.board;

public enum PushDirection {
    TOP_LEFT(-1, 2), TOP(0, 2), TOP_RIGHT(1, 2),
    BOT_LEFT(-1, -2), BOT(0, -2), BOT_RIGHT(1, -2),
    NONE(0, 0);

    private final int deltaX;
    private final int deltaY;

    PushDirection(final int deltaX, final int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }
}
