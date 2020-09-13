package de.flojo.jam.game.board.imprints;

import java.awt.Point;

public abstract class ImprintNodeMap<T> implements ImprintNode<T> {


    protected final Imprint<T> imprint;
    protected final String imprintSupplierName;
    protected final Point pos;

    public ImprintNodeMap(String supplierName, int x, int y) {
        this.imprintSupplierName = supplierName;
        this.pos = new Point(x, y);
        this.imprint = null;
    }

    public ImprintNodeMap(Imprint<T> imprint, Point pos) {
        this.imprint = imprint;
        this.pos = pos;
        this.imprintSupplierName = null;
    }

    @Override
    public Point getPos() {
        return pos;
    }


    
}
