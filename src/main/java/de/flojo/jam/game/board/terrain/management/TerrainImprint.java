package de.flojo.jam.game.board.terrain.management;

import de.flojo.jam.game.board.imprints.Imprint;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.graphics.renderer.IRenderData;

import java.awt.Image;
import java.awt.Point;

public class TerrainImprint extends Imprint<TerrainTile> {

    private static final long serialVersionUID = -7701536199670624780L;

    private final TerrainData data;
    private final TerrainTile baseType;

    public TerrainImprint(TerrainData data, final Point anchor) {
        super(data, anchor);
        this.data = data;
        this.baseType = this.data.getTerrainAt(anchor.x, anchor.y);
    }

    public static TerrainImprint getSingle(TerrainTile type) {
        return new TerrainImprint(new TerrainData(type), new Point(0, 0));
    }

    public TerrainData getData() {
        return data;
    }

    public boolean hasBaseResource() {
        return baseType.getRenderer().hasImage();
    }

    public Image getBaseResource() {
        return baseType.getRenderer().getImageScaled();
    }

    public IRenderData getRenderer() {
        return baseType.getRenderer();
    }

}
