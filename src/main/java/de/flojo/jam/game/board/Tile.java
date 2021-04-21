package de.flojo.jam.game.board;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.mask.DefaultBoardMask;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainTypeSupplier;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Hexagon;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Tile extends Hexagon implements IHaveDecorations, IAmMoveable {

    public static final int DEFAULT_RADIUS = 30;

    private static final Font NUMBER_FONT = Main.TEXT_NORMAL.deriveFont(20f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.6f, 0.6f, 0.3f, 0.25f);
    private static final Color MARK_COLOR = new Color(0.3f, 0.6f, 0.3f, 0.4f);
    private static final Color NONE_COLOR = new Color(0, 0, 0, 35); // 154, 215, 45
    private static final Color P1_COLOR = new Color(45, 173, 215, 35);
    private static final Color P2_COLOR = new Color(141, 45, 215, 35);
    private static final long serialVersionUID = 9075282633765587910L;
    private final String tileLabel;
    private final PlayerId placementOwner;
    private final Color ownerColor;
    private final BoardCoordinate coordinate;
    private final transient TerrainTypeSupplier terrainSupplier;
    private final AtomicBoolean hover = new AtomicBoolean();
    private final AtomicBoolean mark = new AtomicBoolean();
    private Set<Tile> neighbours;
    private final int origX;
    private final int origY;
    public Tile(BoardCoordinate coordinate, int x, int y, TerrainTypeSupplier type) {
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
            this.draw(g, 5, MARK_COLOR, true);

        this.draw(g, 3, new Color(0.4f, 0.6f, 0.3f, 0.15f), false);
        if (showCordData) {
            g.setColor(Color.WHITE);
            g.setFont(NUMBER_FONT);
            TextRenderer.render(g, tileLabel, getCenter().x - TextRenderer.getWidth(g, tileLabel) / 2 + getShiftX(), getCenter().y + TextRenderer.getHeight(g, tileLabel) * 0.15 + getShiftY());
        }
    }

    public void updateZoom(float newZoom) {
        this.setRadius((int) (DEFAULT_RADIUS * newZoom));
        this.setCenter((int)(origX*newZoom), (int)(origY*newZoom));
    }

    @Override
    public void renderDecorations(Graphics2D g) {
        TerrainTile tt = terrainSupplier.getTerrainAt(coordinate);
        if (tt != null)
            tt.render(g, getShiftedCenter(), getHint());
    }

    public Point2D getShiftedCenter() {
        final Point2D center = getCenter();
        return new Point((int)(center.getX() + getShiftX()), (int)(center.getY() + getShiftY()));
    }

    private RenderHint getHint() {
        if (hover.get())
            return RenderHint.HOVER;
        return mark.get() ? RenderHint.MARKED : RenderHint.NORMAL;
    }

    @Override
    public String toString() {
        return "Tile [coordinate=" + coordinate + ", terrainType=" + getTerrainType() + "]";
    }

    public TerrainTile getTerrainType() {
        return terrainSupplier.getTerrainAt(coordinate);
    }

}