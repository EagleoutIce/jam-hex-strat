package de.flojo.jam.game.board.terrain.management;

import java.awt.Point;

public class ImprintNodeMap implements ImprintNode {


    private final TerrainImprint imprint;
    private final String terrainImprintSupplierName;
    private final Point pos;

    public ImprintNodeMap(String terrainImprintSupplierName, int x, int y) {
        this.terrainImprintSupplierName = terrainImprintSupplierName;
        this.pos = new Point(x, y);
        this.imprint = null;
    }

    public ImprintNodeMap(TerrainImprint imprint, Point pos) {
        this.imprint = imprint;
        this.pos = pos;
        this.terrainImprintSupplierName = null;
    }

    @Override
    public TerrainImprint getImprint() {
        // get late to achieve late binding?
        return imprint == null ? TerrainId.valueOf(terrainImprintSupplierName).getImprint() : imprint;
    }

    @Override
    public Point getPos() {
        return pos;
    }


    
}
