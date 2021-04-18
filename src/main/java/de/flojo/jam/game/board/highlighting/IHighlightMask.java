package de.flojo.jam.game.board.highlighting;

import java.awt.*;
import java.io.Serializable;

public interface IHighlightMask extends Serializable {
    boolean[][] getGrid();

    Point getAnchor();
}
