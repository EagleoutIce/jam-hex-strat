package de.flojo.jam.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.game.terrain.TerrainType;
import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

public class Tile extends Hexagon implements IRenderable, IHaveDecorations, IAmMoveable, IAmNode {

    // TODO: decorations

    public static final int DEFAULT_RADIUS = 30;
    private BoardCoordinate coordinate;
    private TerrainType terrainType;

    private AtomicBoolean hover = new AtomicBoolean();

    public Tile(BoardCoordinate coordinate, int x, int y, TerrainType type) {
        super(x, y, DEFAULT_RADIUS);
        this.coordinate = coordinate;
        this.terrainType = type;
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

    public boolean isNeighbour(Tile other) {
        return this.getCenter().distance(other.getCenter()) <= 2.5 * DEFAULT_RADIUS;
    }

    @Override
    public void render(Graphics2D g) {
        if (hover.get()) {
            this.draw(g, 0, new Color(0.6f, 0.6f, 0.3f, 0.2f), true);
        }
        this.draw(g, 4, new Color(0.4f, 0.6f, 0.3f, 0.6f), false);
        final String cord = coordinate.getX() + " + " + coordinate.getY();
        TextRenderer.render(g, cord, getCenter().x - TextRenderer.getWidth(g, cord) / 2, getCenter().y);
    }

    @Override
    public void renderDecorations(Graphics2D g) {
        terrainType.render(g, getCenter(), hover.get());
    }

    @Override
    public void move(int rx, int ry) {
        super.move(rx, ry);
        // TODO: other
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tile [coordinate=").append(coordinate).append(", terrainType=").append(terrainType).append("]");
        return builder.toString();
    }

}
