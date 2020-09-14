package de.flojo.jam.game.board.traps;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

import de.flojo.jam.game.board.traps.management.TrapData;
import de.flojo.jam.game.board.traps.management.TrapTile;

public enum TrapId {
    T_SPIKE("SpikeTrap", TrapImprint.getSingle(TrapTile.SPIKE), Color.MAGENTA),
    T_BEAR_TRAP("SpikeTrap", new TrapImprint(new TrapData(//
        Arrays.asList(//
            Arrays.asList(TrapTile.BEAR_TRAP_GHOST, null), //
            Arrays.asList(null, TrapTile.BEAR_TRAP) //
        )), new Point(1, 1)), Color.GREEN);
    
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
