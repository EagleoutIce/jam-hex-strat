package de.flojo.jam.game.board;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import de.flojo.jam.game.board.highlighting.IHighlightMask;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class Board implements IRenderable, IAmMoveable, IAmNode, Serializable, MouseMotionListener {
    private static final long serialVersionUID = 6531704891590315776L;

    private final transient BufferedImage background;

    private final TerrainMap terrainMap;

    public static final int PADDING = 0;

    private final int width;
    private final int height;
    private final String backgroundPath;
    private int shiftX;
    private int shiftY;
    private final Point tilesUpperLeft;

    private IHighlightMask highlightMask;

    private final Map<BoardCoordinate, Tile> tiles;

    public Board(final int w, final int h, final String backgroundPath, final String terrainPath) {
        this.width = w;
        this.height = h;
        this.backgroundPath = backgroundPath;
        this.background = Resources.images().get(backgroundPath);
        this.tiles = new HashMap<>(w * h);
        this.terrainMap = new TerrainMap(w, h, terrainPath);
        tilesUpperLeft = getTilesUpperLeft();
        setupTiles();
        setupResizeListener();
        initialShifts();
        highlightMask = SimpleHighlighter.get();
        setupInput();
        Game.log().log(Level.INFO, "Loaded Board with background: \"{0}\"", this.backgroundPath);
    }

    private void initialShifts() {
        move(getBackgroundOffsetPosX() / 2, 0);
        move(0, getBackgroundOffsetPosY() / 2);
    }

    private void setupResizeListener() {
        Game.window().onResolutionChanged(r -> {
            if (Game.window().getHeight() - shiftY >= background.getHeight() - 5) {
                int offset = getBackgroundOffsetPosY();
                move(0, offset - shiftY);
            }

            if (Game.window().getWidth() - shiftX >= background.getWidth() - 5) {
                int offset = getBackgroundOffsetPosX();
                move(offset - shiftX, 0);
            }
        });
    }

    private int getBackgroundOffsetPosX() {
        return Math.min(0, Game.window().getWidth() - background.getWidth() - 5);
    }

    private int getBackgroundOffsetPosY() {
        return Math.min(0, Game.window().getHeight() - background.getHeight() - 5);
    }

    private Point getTilesUpperLeft() {
        final double topWidth = Tile.getWidth() - 2 * Tile.getSegmentWidth(); // ----
        final double startX = freeSpaceVertical(topWidth) / 2 + Tile.getWidth() / 2; // drawn centered
        final double startY = background.getHeight() / 2d - height / 4d * Tile.getHeight();
        return new Point((int) startX, (int) startY);
    }

    private double freeSpaceVertical(final double topWidth) {
        return background.getWidth()
                - (Math.ceil(width / 2d) * Tile.getWidth() + (Math.ceil(width / 2d) - 1) * topWidth);
    }


    public synchronized void setHighlightMask(IHighlightMask mask) {
        this.highlightMask = mask;
    }

    private void setupInput() {
        Input.mouse().addMouseMotionListener(this);
        // TODO: Input controller only one key at a time
        Input.keyboard().onKeyPressed(KeyEvent.VK_W, e -> {
            if (shiftY <= -5) {
                shiftY += 5;
                tiles.forEach((c, h) -> h.move(0, 5));
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_A, e -> {
            if (shiftX <= -5) {
                tiles.forEach((c, h) -> h.move(5, 0));
                shiftX += 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_S, e -> {
            if (Game.window().getHeight() - shiftY < background.getHeight() - 5) {
                tiles.forEach((c, h) -> h.move(0, -5));
                shiftY -= 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_D, e -> {
            if (Game.window().getWidth() - shiftX < background.getWidth() - 5) {
                tiles.forEach((c, h) -> h.move(-5, 0));
                shiftX -= 5;
            }
        });
    }

    private int lineToggle(final int row) {
        return (row + 1) % 2;
    }

    private void setupTiles() {
        tiles.clear();
        populateTiles();
        populateNeighbours();
    }

    private void populateNeighbours() {
        for (final Tile tile : tiles.values()) {
            final Set<Tile> neighbours = tiles.values().stream().filter(tile::isNeighbour)
                    .collect(Collectors.toCollection(HashSet::new));
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
            int colStart = lineToggle(row);
            for (int col = colStart; col < width - lineToggle(row); col+=2) {
                int effectiveRow = Math.floorDiv(row, 2);
                int visualCol = Math.floorDiv(col, 2);
                final double x = tilesUpperLeft.getX() + visualCol * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift;
                final double y = tilesUpperLeft.getY() + row * (0.5 * hexHeight + PADDING);
                tiles.put(new BoardCoordinate(col, effectiveRow), new Tile(new BoardCoordinate(col, effectiveRow), (int) x, (int) y, terrainMap.getTerrainAt(col, row)));
            }
        }
    }

    public void move(final int rx, final int ry) {
        this.shiftX += rx;
        this.shiftY += ry;
        tiles.forEach((c,t) -> t.move(rx, ry));
    }

    private Tile findAndClearHovered(final MouseEvent e) {
        Tile foundTile = null;
        for (final Tile tile : tiles.values()) {
            if (foundTile == null && tile.contains(e.getX(), e.getY())) {
                foundTile = tile;
            } else {
                tile.clearHover();// clear all
            }
        }
        return foundTile;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        Tile hoveredTile = findAndClearHovered(e);
        if(hoveredTile == null)
            return;
        
        // apply hover mask
        boolean[][] hl = this.highlightMask.getGrid();
        Point anchor = this.highlightMask.getAnchor();
        BoardCoordinate hPoint = hoveredTile.getCoordinate();

        for (int y = 0; y < hl.length; y++) {
            for (int x = 0; x < hl[y].length; x++) {
                if(hl[y][x]) {
                    // transform target in boardCoordinates
                    BoardCoordinate target = new BoardCoordinate(hPoint.x + x - anchor.x, hPoint.y + y - anchor.y);
                    Tile targetTile = tiles.get(target);
                    if(targetTile != null) {
                        targetTile.setHover();
                    }
                }
            }
        }
    }

    @Override
    public void render(final Graphics2D g) {
        ImageRenderer.render(g, background, shiftX, shiftY);
        for (final Tile tile : tiles.values())
            tile.render(g);

        for (final Tile tile : tiles.values())
            tile.renderDecorations(g);
    }

    @Override
    public IAmNode getChildren() {
        // TODO return tiles
        return null;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        // Do nothing for now
    }

}