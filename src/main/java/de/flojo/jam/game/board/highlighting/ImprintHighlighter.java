package de.flojo.jam.game.board.highlighting;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.flojo.jam.game.board.imprints.Imprint;

public class ImprintHighlighter implements IHighlightMask {

    private static final long serialVersionUID = -3444826491775345869L;

    private final Imprint<?> imprint;

    private boolean[][] grid;

    public ImprintHighlighter(Imprint<?> imprint) {
        this.imprint = imprint;
        setupGrid();
    }

    private void setupGrid() {
        BufferedImage bufImg = imprint.getBitMap();
        grid = new boolean[bufImg.getHeight()][bufImg.getWidth()];
        final int wRgb = Color.WHITE.getRGB();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                grid[y][x] = bufImg.getRGB(x, y) == wRgb;
            }
        }
    }

    @Override
    public boolean[][] getGrid() {
        return grid;
    }

    @Override
    public Point getAnchor() {
        return imprint.getAnchor();
    }


}
