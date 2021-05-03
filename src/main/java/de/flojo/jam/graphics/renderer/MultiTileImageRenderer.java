package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.util.Direction;
import de.flojo.jam.util.HexMaths;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiTileImageRenderer implements IRenderTileData {
    protected final double offsetX;
    protected final double offsetY;
    protected final float scale;
    private final String terrainId;

    private final HashMap<Integer, SimpleImageRenderer> images = new HashMap<>();

    public MultiTileImageRenderer(final Map<String, Integer> images, String terrainId, final double offsetX,
                                  final double offsetY, final float scale) {
        this(images, terrainId, offsetX, offsetY, scale, false);
    }
    public MultiTileImageRenderer(final Map<String, Integer> images, String terrainId, final double offsetX,
                                  final double offsetY, final float scale, final boolean forceLoad) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.terrainId = terrainId;
        images.forEach((n, i) -> this.images.put(i, new SimpleImageRenderer(n, offsetX, offsetY, scale, forceLoad)));
    }

    private static int toNum(boolean x) {
        return toNum(x, 1);
    }

    private static int toNum(boolean x, int g) {
        return x ? g : 0;
    }

    public static int imgIdx(boolean u, boolean ur, boolean lr, boolean l, boolean ll, boolean ul) {
        return toNum(u) + toNum(ur) * 2 + toNum(lr) * 4 + toNum(l) * 8 + toNum(ll) * 16 + toNum(ul) * 32;
    }

    public static int imgIdx(Direction dir) {
        switch (dir) {
            case UP:
                return 1;
            case UP_RIGHT:
                return 2;
            case DOWN_RIGHT:
                return 4;
            case DOWN:
                return 8;
            case DOWN_LEFT:
                return 16;
            case UP_LEFT:
                return 32;
            case NONE:
            default:
                return 0;
        }
    }

    private int getImageIndex(Tile base, Set<Tile> neighbours) {
        var idx = 0;
        for (Tile t : neighbours) {
            if (t.getTerrainType().getNode().getImprintSupplierName().equals(terrainId)) {
                // calculate dir
                final var dir = HexMaths.decodeDirection(base.getCoordinate(), t.getCoordinate());
                idx += imgIdx(dir);
            }
        }
        return idx;
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
        int index = getImageIndex(tile, tile.getNeighbours());
        this.images.getOrDefault(index, images.get(0)).render(g, pos, tile, hints);
    }
}
