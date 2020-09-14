package de.flojo.jam.game.board.mask;

public class RectangleMask {
    
    private int sx;
    private int sy;
    private int ex;
    private int ey;

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
