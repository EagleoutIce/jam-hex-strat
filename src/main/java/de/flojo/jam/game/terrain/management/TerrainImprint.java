package de.flojo.jam.game.terrain.management;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import de.flojo.jam.game.terrain.TerrainType;

public class TerrainImprint {

    private final TerrainData data;
    private final TerrainType baseType;
    private final BufferedImage bitMap;

    // image drawing part
    Point anchor;

    public TerrainImprint(TerrainData data, final Point anchor) {
        this.data = data;
        this.anchor = anchor;
        bitMap = new BufferedImage(data.getWidth(), data.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < data.getHeight(); y++) {
            List<TerrainType> tl = data.get(y);
            for (int x = 0; x < data.getWidth(); x++) {
                TerrainType type = tl.get(x);
                if(type != null)
                    bitMap.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        this.baseType = this.data.getTerrainAt(anchor.x, anchor.y);
    }

    public TerrainData getData() {
        return data;
    }

    public Point getAnchor() {
        return this.anchor;
    }

    public BufferedImage getBitMap() {
        return bitMap;
    }

    public boolean hasBaseResource() {
        return baseType.getRenderer().hasImage();
    }

    public BufferedImage getBaseResource() {
        return baseType.getRenderer().getImage();
    }

    public static final TerrainImprint getSingle(TerrainType type) { return new TerrainImprint(new TerrainData(type), new Point(0,0)); }

}
