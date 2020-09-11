package de.flojo.jam.game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class Board implements IRenderable, IAmMoveable, IAmNode, Serializable,  MouseMotionListener {
    private static final long serialVersionUID = 6531704891590315776L;

    private final transient BufferedImage background;

    public static final int PADDING = 0;

    private int width;
    private int height;
    private String backgroundPath;
    private int shiftX;
    private int shiftY;
    private final Point tilesUpperLeft;

    
    private final List<Tile> tiles;

    public Board(int w, int h, String backgroundPath) {
        this.width = w;
        this.height = h;
        this.backgroundPath = backgroundPath; 
        this.background = Resources.images().get(backgroundPath);
        this.tiles = new ArrayList<>(w * h);
        tilesUpperLeft = getTilesUpperLeft();
        setupTiles();
        setupInput();
        // TODO: resize listener
    }

    private Point getTilesUpperLeft() {
        double topWidth = Tile.getWidth() - 2*Tile.getSegmentWidth(); // ----
        double startX = freeSpaceVertical(topWidth)/2 + Tile.getWidth()/2; // drawn centered
        double startY = background.getHeight()/2d - height/4d * Tile.getHeight() + Tile.getHeight()/2;
        return new Point((int)startX, (int)startY);
    }

    private double freeSpaceVertical(double topWidth) {
        return background.getWidth()-(Math.ceil(width/2d)*Tile.getWidth() + (Math.ceil(width/2d)-1) * topWidth);
    }

    private void setupInput() {
        Input.mouse().addMouseMotionListener(this);
        Input.keyboard().onKeyPressed(KeyEvent.VK_W, e -> {
            if(shiftY <=-5){
                shiftY += 5;
                tiles.forEach(h -> h.move(0, 5));
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_A, e -> {
            if(shiftX <= -5){
                tiles.forEach(h -> h.move(5, 0));
                shiftX += 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_S, e -> {
            if(Game.window().getHeight() - shiftY  < background.getHeight() - 5){
                tiles.forEach(h -> h.move(0, -5));
                shiftY -= 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_D, e -> {
            if(Game.window().getWidth() - shiftX  < background.getWidth() - 5){
                tiles.forEach(h -> h.move(-5, 0));
                shiftX -= 5;
            }
        });
    }

    private int lineToggle(int row) {
        return (row + 1) % 2;
    }

    private void setupTiles() {
        tiles.clear();
        populateTiles();
        populateNeighbours();
    }

    private void populateNeighbours() {
        for (Tile tile : tiles) {
            Set<Tile> neighbours = tiles.stream().filter(tile::isNeighbour).collect(Collectors.toCollection(HashSet::new));
            tile.setNeighbours(neighbours);
        }
    }

    private void populateTiles() {
        final double hexWidth = Tile.getWidth();
        final double hexHeight = Tile.getHeight();
        final double hexSeg = Tile.getSegmentWidth();
        final double rowShift = hexWidth - hexSeg + PADDING;
        final double hexMidWidth = hexWidth - 2 * hexSeg;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < Math.ceil(width / 2d) - lineToggle(row); col++) {
                double x = tilesUpperLeft.getX() + col * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift;
                double y = tilesUpperLeft.getY() + row * (0.5 * hexHeight + PADDING);
                tiles.add(new Tile(new BoardCoordinate(col, row), (int) x, (int) y));
            }
        }
    }

    public void move(int rx, int ry) {
        this.shiftX += rx;
        this.shiftY += ry;
        tiles.forEach(t -> t.move(rx, ry));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boolean hovered = false;
        Tile hoveredTile = null;
        for (Tile tile : tiles) {
            if (!hovered && tile.contains(e.getX(), e.getY())) {
                tile.setHover();
                hoveredTile = tile;
                hovered = true;
            } else {
                tile.clearHover();
            }
        }
        if(hoveredTile != null)
            hoveredTile.getNeighbours().forEach(Tile::setHover);
    }


    @Override
    public void render(Graphics2D g) {
        ImageRenderer.render(g, background, shiftX, shiftY);
        for (Tile tile : tiles) {
            tile.render(g);
        }
    }

    @Override
    public IAmNode getChildren() {
        // TODO return tiles
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing for now
    }


}
