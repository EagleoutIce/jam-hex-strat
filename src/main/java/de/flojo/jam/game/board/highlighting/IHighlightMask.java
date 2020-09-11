package de.flojo.jam.game.board;

import java.awt.Point;
import java.io.Serializable;

public interface IHighlightMask extends Serializable {
    
    public boolean[][] getGrid();
    public Point getAnchor();

}
