package de.flojo.jam.game.board.highlighting;

import java.awt.*;
import java.io.Serializable;

public interface IHighlightMask extends Serializable {
    public boolean[][] getGrid();

    public Point getAnchor();
}
