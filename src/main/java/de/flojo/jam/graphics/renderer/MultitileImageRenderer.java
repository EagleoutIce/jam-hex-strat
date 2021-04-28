package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.terrain.management.TerrainIdConstants;
import de.flojo.jam.game.board.terrain.management.TerrainTypeSupplier;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultitileImageRenderer implements IRenderTileData {
    protected final double offsetX;
    protected final double offsetY;
    protected final float scale;
    private final String terrainId;

    private final HashMap<Integer, SimpleImageRenderer> images = new HashMap<>();


    private static int toNum(boolean x) { return toNum(x, 1); }
    private static int toNum(boolean x, int g) { return x ? g : 0; }

    public static int imgIdx(boolean u, boolean ur, boolean lr, boolean l, boolean ll, boolean ul) {
        return toNum(u) + toNum(ur) * 2 + toNum(lr) * 2^2 + toNum(l) * 2^3 + toNum(ll) * 2^4 + toNum(ul) * 2^5;
    }

    private int getImageIndex(Set<Tile> neighbours) {
        var idx = 0;
        for (Tile t: neighbours) {
            if(t.getTerrainType().getNode().getImprintSupplierName().equals(terrainId)) {
                // calculate dir
            }
        }
        return idx;
    }

    public MultitileImageRenderer(final Map<String, Integer> images, String terrainId, final double offsetX, final double offsetY, final float scale) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.terrainId = terrainId;
        images.forEach((n, i) -> this.images.put(i, new SimpleImageRenderer(n, offsetX, offsetY, scale)));
    }


    private float scale() {
        return Board.getZoom() * scale;
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public BufferedImage getImage() {
        return images.get(0).getImage();
    }

    @Override
    public Image getImageScaled() {
        return images.get(0).getImageScaled();
    }

    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return images.get(0).getEffectiveRectangle(pos);
    }

    @Override
    public double getOffsetX() {
        return offsetX;
    }

    @Override
    public double getOffsetY() {
        return offsetY;
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, final Tile tile, final RenderHint... hints) {
        int index = getImageIndex(tile.getNeighbours());
        this.images.getOrDefault(index, images.get(0)).render(g, pos, tile, hints);
    }
}
