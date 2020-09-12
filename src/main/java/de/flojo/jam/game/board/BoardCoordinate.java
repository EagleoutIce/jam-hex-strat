package de.flojo.jam.game.board;

import java.awt.Point;

public class BoardCoordinate extends Point {

    private static final long serialVersionUID = 2749088523123720359L;

    public BoardCoordinate() {
        super();
    }

    public BoardCoordinate(Point p) {
        super(p);
    }

    public BoardCoordinate(int x, int y) {
        super(x, y);
    }

    public BoardCoordinate translateRelative(int rx, int ry) {
        int mX = Math.floorMod(x, 2);
        int mY = Math.floorMod(y, 2);
        if (rx != 0) { // marker kompensation
            if (mX == 1 && mY == 1 && rx > 0) // ungerade/ungerade
                return new BoardCoordinate(x + rx - 1, y + ry);
            else if (mX == 1 && mY == 0 && rx < 0) // ungarade/gerade
                return new BoardCoordinate(x + rx + 1, y + ry);
            else if (mX == 0 && mY == 0 && rx < 0) // garade/gerade
                return new BoardCoordinate(x + rx + 1, y + ry);
            else if (mX == 0 && mY == 1 && rx > 0)// garade/ungerade
                return new BoardCoordinate(x + rx -1, y + ry);
        }
        return new BoardCoordinate(x + rx, y + ry);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(BC@").append(x).append(",").append(y).append(")");
        return builder.toString();
    }

}
