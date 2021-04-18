package de.flojo.jam.game.board.mask;

public class RectangleMask {
    private final int sx;
    private final int sy;
    private final int ex;
    private final int ey;

    public RectangleMask(int sx, int sy, int ex, int ey) {
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
    }

    public boolean covers(int x, int y) {
        return sx <= x && ex >= x && sy <= y && ey >= y;
    }
}
