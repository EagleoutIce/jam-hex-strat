package de.flojo.jam.game.board.traps.management;

import java.awt.Point;

import de.flojo.jam.game.board.imprints.ImprintNodeMap;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapImprint;

public class TrapImprintNodeMap extends ImprintNodeMap<TrapTile> {
    public TrapImprintNodeMap(String supplierName, int x, int y) {
        super(supplierName, x, y);
    }

    public TrapImprintNodeMap(TrapImprint imprint, Point pos) {
        super(imprint, pos);
    }
    

    @Override
    public TrapImprint getImprint() {
        // get late to achieve late binding?
        return imprint == null ? TrapId.valueOf(imprintSupplierName).getImprint() : (TrapImprint)imprint;
    }
}

