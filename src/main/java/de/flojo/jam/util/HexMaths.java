package de.flojo.jam.util;

import de.flojo.jam.game.board.BoardCoordinate;
import de.gurkenlabs.litiengine.Game;

import java.util.logging.Level;

// Hexagon maths :D
public class HexMaths {

    private HexMaths() {
        throw new UnsupportedOperationException();
    }

    public static int effectiveWidth(int w) {
        return (int) (Math.ceil(w / 2d));
    }

    private static int normalizeDistDelta(int a, int b) {
        if (a == b)
            return 0;
        return a > b ? 1 : -1;
    }

    public static BoardCoordinate decodeDelta(int aX, int aY, int tX, int tY) {
        return decodeDelta(new BoardCoordinate(aX, aY), new BoardCoordinate(tX, tY));
    }

    // gerade ungerade, kleiner kleiner
    public static BoardCoordinate decodeDelta(BoardCoordinate a, BoardCoordinate t) {
        if (a.x == t.x && a.y == t.y)
            return new BoardCoordinate(0, 0);

        int dX = normalizeDistDelta(t.x, a.x);
        int dY = normalizeDistDelta(t.y, a.y);
        int mX = Math.floorMod(t.x, 2);
        int mY = Math.floorMod(t.y, 2);

        if (dX == 0) {
            return getDeltaForNoX(a, t, dY, mX, mY);
        } else if (dX > 0) {
            if (dY < 0) {
                return new BoardCoordinate(dX, -1);
            } else if (dY == 0) {
                HexStartLogger.log().log(Level.SEVERE, "dX > 0 && dY == 0 steppe from a {0} to b {1}", new Object[]{a, t});
            }
        } else if (dY == 0) {
            HexStartLogger.log().log(Level.SEVERE, "dX < 0 && dY == 0 steppe from a {0} to b {1}", new Object[]{a, t});
        } else if (dY < 0) {
            return new BoardCoordinate(mY == 0 ? -1 : 1, dY);
        }
        return new BoardCoordinate(dX, dY);
    }

    private static BoardCoordinate getDeltaForNoX(BoardCoordinate a, BoardCoordinate t, int dY, int mX, int mY) {
        if (mY == Math.floorMod(a.y, 2)) { // vertical!!
            return new BoardCoordinate(0, a.y > t.y ? -2 : 2);
        }
        return getDeltaForNoXDiagonal(dY, mY, mX);
    }


    private static BoardCoordinate getDeltaForNoXDiagonal(int dY, int mY, int mX) {
        if (dY < 0)
            return new BoardCoordinate(((mX == 0 && mY == 1) || (mX == 1 && mY == 1)) ? -1 : 1, dY);
        else
            return new BoardCoordinate(mY == 0 ? 1 : -1, dY);
    }
}
