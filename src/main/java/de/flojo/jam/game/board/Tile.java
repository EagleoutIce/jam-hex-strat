package de.flojo.jam.game.board;

import de.flojo.jam.game.board.mask.DefaultBoardMask;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainTypeSupplier;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Hexagon;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.flojo.jam.game.TileConstants.DEFAULT_COLOR;
import static de.flojo.jam.game.TileConstants.DEFAULT_RADIUS;
import static de.flojo.jam.game.TileConstants.HIGHLIGHT_COLOR;
import static de.flojo.jam.game.TileConstants.MARK_COLOR;
import static de.flojo.jam.game.TileConstants.NONE_COLOR;
import static de.flojo.jam.game.TileConstants.NUMBER_FONT;
import static de.flojo.jam.game.TileConstants.P1_COLOR;
import static de.flojo.jam.game.TileConstants.P2_COLOR;

public class Tile extends Hexagon implements IHaveDecorations, IAmMoveable {
    private final String tileLabel;
    private final PlayerId placementOwner;
    private final Color ownerColor;
    private final BoardCoordinate coordinate;
    private final transient TerrainTypeSupplier terrainSupplier;
    private final AtomicBoolean hover = new AtomicBoolean();
    private final AtomicBoolean mark = new AtomicBoolean();
    private final double origX;
    private final double origY;
    private float currentZoom = 1f;
    private Set<Tile> neighbours;

    public Tile(BoardCoordinate coordinate, double x, double y, TerrainTypeSupplier type) {
        super(x, y, DEFAULT_RADIUS);
        this.origX = x;
        this.origY = y;
        this.coordinate = coordinate;
        this.terrainSupplier = type;
        this.placementOwner = DefaultBoardMask.get().getOwner(coordinate);
        if (this.placementOwner == null)
            ownerColor = NONE_COLOR;
        else
            ownerColor = this.placementOwner.ifOne(P1_COLOR, P2_COLOR);
        tileLabel = coordinate.x + "/" + coordinate.y;
    }

    public static double getHeight() {
        return getHeightOf(DEFAULT_RADIUS);
    }

    public static double getWidth() {
        return getWidthOf(DEFAULT_RADIUS);
    }

    public static double getSegmentWidth() {
        return getSegmentWidthOf(DEFAULT_RADIUS);
    }

    public Set<Tile> getNeighbours() {
        return this.neighbours;
    }

    public void setNeighbours(Set<Tile> neighbours) {
        this.neighbours = neighbours;
    }

    public BoardCoordinate getCoordinate() {
        return this.coordinate;
    }

    public void setHover() {
        this.hover.set(true);
    }

    public void clearHover() {
        this.hover.set(false);
    }

    public boolean isHovered() {
        return hover.get();
    }

    public boolean isNeighbour(Tile other) {
        return this.getCenter().distance(other.getCenter()) <= 2.5 * DEFAULT_RADIUS;
    }

    public void mark(boolean doMark) {
        this.mark.set(doMark);
    }

    public boolean isMarked() {
        return this.mark.get();
    }

    public PlayerId getPlacementOwner() {
        return placementOwner;
    }

    public void render(Graphics2D g, boolean showCordData) {
        if (showCordData)
            this.draw(g, 0, ownerColor, true);
        if (hover.get())
            this.draw(g, 0, HIGHLIGHT_COLOR, true);
        if (mark.get())
            this.draw(g, 0, MARK_COLOR, true);

        this.draw(g, 3 * currentZoom, DEFAULT_COLOR, false);
        if (showCordData) {
            g.setColor(Color.WHITE);
            g.setFont(NUMBER_FONT);
            TextRenderer.render(g, tileLabel,
                                getCenter().getX() - TextRenderer.getWidth(g, tileLabel) / 2 + getShiftX(),
                                getCenter().getY() + TextRenderer.getHeight(g, tileLabel) * 0.15 + getShiftY());
        }
    }

    public void updateZoom(float newZoom) {
        currentZoom = newZoom;
        this.setRadius((int) (DEFAULT_RADIUS * newZoom));
        this.setCenter(origX * newZoom, origY * newZoom);
    }

    @Override
    public void renderDecorations(Graphics2D g) {
        TerrainTile tt = terrainSupplier.getTerrainAt(coordinate);
        if (tt != null)
            tt.render(g, getShiftedCenter(), getHint());
    }

    public Point2D getShiftedCenter() {
        final Point2D center = getCenter();
        return new Point2D.Double(center.getX() + getShiftX(), center.getY() + getShiftY());
    }

    private RenderHint getHint() {
        if (hover.get())
            return RenderHint.HOVER;
        return mark.get() ? RenderHint.HOVER : RenderHint.NORMAL;
    }

    @Override
    public String toString() {
        return "Tile [coordinate=" + coordinate + ", terrainType=" + getTerrainType() + "]";
    }

    public TerrainTile getTerrainType() {
        return terrainSupplier.getTerrainAt(coordinate);
    }
}
