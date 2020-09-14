package de.flojo.jam.game.board.highlighting;

import java.awt.Point;

public final class SimpleHighlighter implements IHighlightMask {

    private static final long serialVersionUID = -4881823468464682506L;

    private SimpleHighlighter() {
    }

    private static final SimpleHighlighter INSTANCE = new SimpleHighlighter();

    public static SimpleHighlighter get() {
        return INSTANCE;
    }

    private boolean[][] grid = {{true}};
    private Point anchor = new Point(0,0);

    @Override
    public boolean[][] getGrid() {
        return grid;
    }

    @Override
    public Point getAnchor() {
        return anchor;    
    }

}
