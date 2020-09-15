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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.highlighting.IHighlightMask;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.util.HexMaths;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.KeyInputGroup;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class Board implements IRenderable, IAmMoveable, Serializable, MouseMotionListener {
    private static final long serialVersionUID = 6531704891590315776L;

    public static final int PADDING = 0;

    private final transient BufferedImage background;

    private TerrainMap terrainMap;

    private final String screenName;

    private final int width;
    private final int height;
    private final String backgroundPath;
    private int shiftX;
    private int shiftY;
    private final Point tilesUpperLeft;

    private IHighlightMask highlightMask;
    private final Map<BoardCoordinate, Tile> tiles;
    private AtomicBoolean doHover = new AtomicBoolean(true);
    private AtomicBoolean showMapDetails = new AtomicBoolean(false);

    private KeyInputGroup bInputGroupVert = new KeyInputGroup();
    private KeyInputGroup bInputGroupHor = new KeyInputGroup();

    public Board(final String terrainPath, final String screenName) {
        this(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, GameField.FIELD_BACKGROUND, terrainPath, screenName);
    }
    public Board(final TerrainMap terrainMap, final String screenName) {
        this(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, GameField.FIELD_BACKGROUND, terrainMap, screenName);
    }
    public Board(final int w, final int h, final String backgroundPath, final String terrainPath, final String screenName) {
        this(w, h, backgroundPath, new TerrainMap(w, h, terrainPath), screenName);
    }

    public Board(final int w, final int h, final String backgroundPath, final TerrainMap terrainMap, final String screenName) {
        this.width = w;
        this.height = h;
        this.backgroundPath = backgroundPath;
        this.screenName = screenName;
        this.background = Resources.images().get(backgroundPath);
        this.tiles = new HashMap<>(w * h);
        this.terrainMap = terrainMap;
        tilesUpperLeft = getTilesUpperLeft();
        setupTiles();
        setupResizeListener();
        initialShifts();
        highlightMask = SimpleHighlighter.get();
        Game.log().log(Level.INFO, "Loaded Board with background: \"{0}\"", this.backgroundPath);
        setupInput();
    }

    public TerrainMap getTerrainMap() {
        return terrainMap;
    }

    public void setTerrainMap(final TerrainMap terrainMap) {
        this.terrainMap = terrainMap;
    }

    private void initialShifts() {
        move(getBackgroundOffsetPosX() / 2, 0);
        move(0, getBackgroundOffsetPosY() / 2);
    }

    private void setupResizeListener() {
        Game.window().onResolutionChanged(r -> {
            if (Game.window().getHeight() - shiftY >= background.getHeight() - 5) {
                final int offset = getBackgroundOffsetPosY();
                move(0, offset - shiftY);
            }

            if (Game.window().getWidth() - shiftX >= background.getWidth() - 5) {
                final int offset = getBackgroundOffsetPosX();
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
        final double startX = freeSpaceVertical(topWidth) / 2 + Tile.getWidth() / 2 - 1.33 * Tile.getSegmentWidth(); // drawn
                                                                                                                     // centered
        final double startY = background.getHeight() / 2d - height / 4d * Tile.getHeight() + Tile.getHeight() / 2.4;
        return new Point((int) startX, (int) startY);
    }

    private double freeSpaceVertical(final double topWidth) {
        return background.getWidth()
                - (HexMaths.effectiveWidth(width) * Tile.getWidth() + (HexMaths.effectiveWidth(width) - 1) * topWidth);
    }

    public synchronized void setHighlightMask(final IHighlightMask mask) {
        this.highlightMask = mask;
    }

    private void setupInput() {
        Input.mouse().addMouseMotionListener(this);

        InputController.get().onKeyPressed(KeyEvent.VK_W, e -> cameraPanUp(),
                Set.of(screenName), bInputGroupVert);
        InputController.get().onKeyPressed(KeyEvent.VK_A, e -> cameraPanLeft(),
                Set.of(screenName), bInputGroupHor);
        InputController.get().onKeyPressed(KeyEvent.VK_S, e -> cameraPanDown(),
                Set.of(screenName), bInputGroupVert);
        InputController.get().onKeyPressed(KeyEvent.VK_D, e -> cameraPanRight(),
                Set.of(screenName), bInputGroupHor);
        InputController.get().onKeyTyped(KeyEvent.VK_M, e -> toggleDataView(),
                Set.of(screenName), bInputGroupHor);
    }

    private void toggleDataView() {
        showMapDetails.set(!showMapDetails.get());
    }

    private void cameraPanRight() {
        if (Game.window().getWidth() - shiftX < background.getWidth() - 5) {
            tiles.forEach((c, h) -> h.move(-5, 0));
            shiftX -= 5;
        }
    }

    private void cameraPanDown() {
        if (Game.window().getHeight() - shiftY < background.getHeight() - 5) {
            tiles.forEach((c, h) -> h.move(0, -5));
            shiftY -= 5;
        }
    }

    private void cameraPanLeft() {
        if (shiftX <= -5) {
            tiles.forEach((c, h) -> h.move(5, 0));
            shiftX += 5;
        }
    }

    private void cameraPanUp() {
        if (shiftY <= -5) {
            shiftY += 5;
            tiles.forEach((c, h) -> h.move(0, 5));
        }
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
            for (int col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                final double x = tilesUpperLeft.getX() + col * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift;
                final double y = tilesUpperLeft.getY() + row * (0.5 * hexHeight + PADDING);
                tiles.put(new BoardCoordinate(col, row), new Tile(new BoardCoordinate(col, row), (int) x, (int) y,
                        c -> terrainMap.getTerrainAt(c.x, c.y)));
            }
        }
    }

    public void move(final int rx, final int ry) {
        this.shiftX += rx;
        this.shiftY += ry;
        tiles.forEach((c, t) -> t.move(rx, ry));
    }

    public Tile findTile(final Point position) {
        return tiles.values().stream().filter(t -> t.contains(position)).findAny().orElse(null);
    }

    public Tile getTile(final int x, final int y) {
        return getTile(new BoardCoordinate(x, y));
    }

    public Tile getTile(final BoardCoordinate coordinate) {
        return tiles.get(coordinate);
    }

    private Tile findHovered(final MouseEvent e) {
        for (final Tile tile : tiles.values()) {
            if (tile.contains(e.getX(), e.getY()))
                return tile;
        }
        return null;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        mouseUpdate(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        mouseUpdate(e);
    }

    public void doHover() {
        this.doHover.set(true);
    }

    public boolean doesHover() {
        return this.doHover.get();
    }

    public void doNotHover() {
        this.doHover.set(false);
    }

    private final Set<Tile> highlightTiles = new HashSet<>();

    private void mouseUpdate(final MouseEvent e) {
        highlightTiles.forEach(Tile::clearHover);
        highlightTiles.clear();
        if (!doHover.get()) {
            return;
        }

        final Tile hoveredTile = findHovered(e);
        if (hoveredTile == null)
            return;

        // apply hover mask
        final boolean[][] hl = this.highlightMask.getGrid();
        final Point anchor = this.highlightMask.getAnchor();
        updateHighlighting(hoveredTile.getCoordinate(), hl, anchor);
    }

    private void updateHighlighting(final BoardCoordinate hPoint, final boolean[][] hl, final Point anchor) {
        highlightTiles.clear();
        if (updateHighlightingRecursive(hPoint, hl, anchor, highlightTiles)) {
            // no highlight if invalid
            highlightTiles.forEach(Tile::setHover);
        }
    }

    // TODO: simplify
    private boolean updateHighlightingRecursive(final BoardCoordinate hPoint, final boolean[][] hl, final Point anchor,
            final Set<Tile> highlightTiles) {
        for (int y = 0; y < hl.length; y++) {
            for (int x = 0; x < hl[y].length; x++) {
                if (!hl[y][x])
                    continue;
                if (!processSingleTileHighlight(hPoint, x, y, anchor, highlightTiles))
                    return false;
            }
        }
        return true;
    }

    private boolean processSingleTileHighlight(BoardCoordinate hPoint, int x, int y, Point anchor,
            Set<Tile> highlightTiles) {
        // transform target in boardCoordinates
        BoardCoordinate effectiveCoordinate = hPoint.translateRelativeX(x - anchor.x, y - anchor.y);
        final Tile targetTile = getTile(effectiveCoordinate);
        TerrainTile targetTerrainType = targetTile == null ? null : targetTile.getTerrainType();

        // invalid as too close to border
        if (targetTile == null) 
            return false;

        if (highlightTiles.add(targetTile)) {
            IHighlightMask recHighlightMask = new ImprintHighlighter(targetTerrainType.getNode().getImprint());
            if (!updateHighlightingRecursive(targetTile.getCoordinate(), recHighlightMask.getGrid(),
                    targetTerrainType.getNode().getPos(), highlightTiles))
                return false;
        }

        return true;
    }

    @Override
    public void render(final Graphics2D g) {
        ImageRenderer.render(g, background, shiftX, shiftY);
        for (final Tile tile : tiles.values())
            tile.render(g, showMapDetails.get());

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                tiles.get(new BoardCoordinate(col, row)).renderDecorations(g);
            }
        }
    }

    public void jointRender(final Graphics2D g, PlayerId renderOwner,  CreatureFactory factory, TrapSpawner traps) {
        ImageRenderer.render(g, background, shiftX, shiftY);
        for (final Tile tile : tiles.values())
            tile.render(g, showMapDetails.get());

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                BoardCoordinate coordinate = new BoardCoordinate(col, row);
                tiles.get(coordinate).renderDecorations(g);
                traps.getRoot(coordinate).ifPresent(t -> t.renderBaseFor(g, renderOwner));
                factory.get(coordinate).ifPresent(c -> c.render(g));
                traps.getRoot(coordinate).ifPresent(t -> t.renderTriggerFor(g, renderOwner));
            }
        }
    }

}
