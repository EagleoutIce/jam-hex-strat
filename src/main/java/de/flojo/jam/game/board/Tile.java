package de.flojo.jam.game.board;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.mask.DefaultBoardMask;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainTypeSupplier;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Hexagon;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

public class Tile extends Hexagon implements IHaveDecorations, IAmMoveable {

    public static final int DEFAULT_RADIUS = 30;

    private static final Font NUMBER_FONT = Main.TEXT_NORMAL.deriveFont(20f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.6f, 0.6f, 0.3f, 0.2f);
    private static final Color MARK_COLOR = new Color(0.3f, 0.6f, 0.3f, 0.4f);
    private static final Color NONE_COLOR = new Color(0, 0, 0, 60); // 154, 215, 45
    private static final Color P1_COLOR = new Color(45, 173, 215, 60);
    private static final Color P2_COLOR = new Color(141, 45, 215, 60);

    private BoardCoordinate coordinate;
    private transient TerrainTypeSupplier terrainSupplier;

    private AtomicBoolean hover = new AtomicBoolean();
    private AtomicBoolean mark = new AtomicBoolean();
    private final String tileLabel;
    private final PlayerId placementOwner;
    private final Color ownerColor;

    public Tile(BoardCoordinate coordinate, int x, int y, TerrainTypeSupplier type) {
        super(x, y, DEFAULT_RADIUS);
        this.coordinate = coordinate;
        this.terrainSupplier = type;
        this.placementOwner = DefaultBoardMask.get().getOwner(coordinate);
        if(this.placementOwner == null)
            ownerColor = NONE_COLOR;
        else
            ownerColor = this.placementOwner.ifOne(P1_COLOR, P2_COLOR);
        tileLabel = coordinate.x + "/"+ coordinate.y;
    }

    private static final long serialVersionUID = 9075282633765587910L;

    public static double getHeight() {
        return getHeightOf(DEFAULT_RADIUS);
    }

    public static double getWidth() {
        return getWidthOf(DEFAULT_RADIUS);
    }

    public static double getSegmentWidth() {
        return getSegmentWidthOf(DEFAULT_RADIUS);
    }

    private Set<Tile> neighbours;

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
        if(showCordData)
            this.draw(g, 0, ownerColor, true);
        if (hover.get()) 
            this.draw(g, 0, HIGHLIGHT_COLOR, true);

        if(mark.get()) 
            this.draw(g, 5, MARK_COLOR, true);

        this.draw(g, 4, new Color(0.4f, 0.6f, 0.3f, 0.6f), false);
        if(showCordData) {
            g.setColor(Color.WHITE);
            g.setFont(NUMBER_FONT);
            TextRenderer.render(g, tileLabel, getCenter().x - TextRenderer.getWidth(g, tileLabel) / 2, getCenter().y + TextRenderer.getHeight(g, tileLabel)*0.15);
        }
    }

    @Override
    public void renderDecorations(Graphics2D g) {
        TerrainTile tt = terrainSupplier.getTerrainAt(coordinate);
        if(tt != null)
            tt.render(g, getCenter(), getHint());
    }

    private RenderHint getHint() {
        if(hover.get())
            return RenderHint.HOVER;
        return mark.get() ? RenderHint.MARKED : RenderHint.NORMAL;
    }

    @Override
    public void move(int rx, int ry) {
        super.move(rx, ry);
        // TODO: other stuff on zoom?
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tile [coordinate=").append(coordinate).append(", terrainType=").append(getTerrainType()).append("]");
        return builder.toString();
    }

    public TerrainTile getTerrainType() {
        return terrainSupplier.getTerrainAt(coordinate);
    }

}