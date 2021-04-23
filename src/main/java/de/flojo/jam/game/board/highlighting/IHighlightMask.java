package de.flojo.jam.game.board.highlighting;

import java.awt.Point;
import java.io.Serializable;

public interface IHighlightMask extends Serializable {
    boolean[][] getGrid();

    Point getAnchor();
}
