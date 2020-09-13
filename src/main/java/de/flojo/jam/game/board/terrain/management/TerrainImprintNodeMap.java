package de.flojo.jam.game.board.terrain.management;

import java.awt.Point;

import de.flojo.jam.game.board.imprints.ImprintNodeMap;
import de.flojo.jam.game.board.terrain.TerrainType;

public class TerrainImprintNodeMap extends ImprintNodeMap<TerrainType> {
    public TerrainImprintNodeMap(String supplierName, int x, int y) {
        super(supplierName, x, y);
    }

    public TerrainImprintNodeMap(TerrainImprint imprint, Point pos) {
        super(imprint, pos);
    }
    

    @Override
    public TerrainImprint getImprint() {
        // get late to achieve late binding?
        return imprint == null ? TerrainId.valueOf(imprintSupplierName).getImprint() : (TerrainImprint)imprint;
    }
}
