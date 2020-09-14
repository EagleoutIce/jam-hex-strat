package de.flojo.jam.game.board;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.terrain.TerrainType;
import de.flojo.jam.game.board.terrain.management.TerrainTypeSupplier;
import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

public class Tile extends Hexagon implements IRenderable, IHaveDecorations, IAmMoveable {

    public static final int DEFAULT_RADIUS = 30;
    private BoardCoordinate coordinate;
    private transient TerrainTypeSupplier terrainSupplier;
    private static final Font NUMBER_FONT = Main.TEXT_NORMAL.deriveFont(20f);

    private static final Color HIGHLIGHT_COLOR = new Color(0.6f, 0.6f, 0.3f, 0.2f);
    private Color backgroundFill = null;

    private AtomicBoolean hover = new AtomicBoolean();
    private final String tileLabel;

    public Tile(BoardCoordinate coordinate, int x, int y, TerrainTypeSupplier type) {
        super(x, y, DEFAULT_RADIUS);
        this.coordinate = coordinate;
        this.terrainSupplier = type;
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

    public void setBackgroundFill(Color backgroundFill) {
        this.backgroundFill = backgroundFill;
    }

    @Override
    public void render(Graphics2D g) {
        if (hover.get()) 
            this.draw(g, 0, HIGHLIGHT_COLOR, true);

        if(backgroundFill != null) 
            this.draw(g, 5, backgroundFill, true);

        this.draw(g, 4, new Color(0.4f, 0.6f, 0.3f, 0.6f), false);
        g.setColor(Color.RED);
        g.setFont(NUMBER_FONT);
        TextRenderer.render(g, tileLabel, getCenter().x - TextRenderer.getWidth(g, tileLabel) / 2, getCenter().y + TextRenderer.getHeight(g, tileLabel)*0.15);
    }

    @Override
    public void renderDecorations(Graphics2D g) {
        TerrainType tt = terrainSupplier.getTerrainAt(coordinate);
        if(tt != null)
            tt.render(g, getCenter(), hover.get());
    }

    @Override
    public void move(int rx, int ry) {
        super.move(rx, ry);
        // TODO: other
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tile [coordinate=").append(coordinate).append(", terrainType=").append(getTerrainType()).append("]");
        return builder.toString();
    }

    public TerrainType getTerrainType() {
        return terrainSupplier.getTerrainAt(coordinate);
    }

}
