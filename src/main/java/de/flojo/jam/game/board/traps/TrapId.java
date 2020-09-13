package de.flojo.jam.game.board.traps;

import java.awt.Color;

public enum TrapId {
    T_SPIKE("SpikeTrap", TrapImprint.getSingle(TrapTile.SPIKE), Color.MAGENTA);
    
    private final TrapImprint imprint;
    private final Color simpleColor;
    private final String name;

    TrapId(final String name, final TrapImprint imprint, Color simpleColor) {
        this.name = name;
        this.imprint = imprint;
        this.simpleColor = simpleColor;
    }


    public String getName() {
        return name;
    }

    public TrapImprint getImprint() {
        return imprint;
    }

    public Color getSimpleColor() {
        return simpleColor;
    }

}
