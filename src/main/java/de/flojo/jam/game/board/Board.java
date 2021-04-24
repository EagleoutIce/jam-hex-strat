package de.flojo.jam.game.board;

import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.highlighting.IHighlightMask;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.util.HexMaths;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.KeyInputGroup;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Board implements IRenderable, IAmMoveable, Serializable, MouseMotionListener {
    public static final int PADDING = 0;
    public static final float BACKGROUND_ZOOM_FACTOR = .5f;
    private static final long serialVersionUID = 6531704891590315776L;
    private static final int PAN_SPEED = 3;
    private static float zoom = 1f;
    private final transient BufferedImage background;
    private final String screenName;
    private final int width;
    private final int height;
    private final String backgroundPath;
    private final Point tilesUpperLeft;
    private final Map<BoardCoordinate, Tile> tiles;
    private final Set<Tile> highlightTiles = new HashSet<>();
    private final AtomicBoolean doHover = new AtomicBoolean(true);
    private final AtomicBoolean showMapDetails = new AtomicBoolean(true);
    private final KeyInputGroup bInputGroupVert = new KeyInputGroup();
    private final KeyInputGroup bInputGroupHor = new KeyInputGroup();
    private float thisZoom = 1f;
    private TerrainMap terrainMap;
    private int shiftX;
    private int shiftY;
    private IHighlightMask highlightMask;

    public Board(final String terrainPath, final String screenName) {
        this(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, GameField.FIELD_BACKGROUND, terrainPath, screenName);
    }

    public Board(final TerrainMap terrainMap, final String screenName) {
        this(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, GameField.FIELD_BACKGROUND, terrainMap, screenName);
    }

    public Board(final int w, final int h, final String backgroundPath, final String terrainPath,
                 final String screenName) {
        this(w, h, backgroundPath, new TerrainMap(w, h, terrainPath), screenName);
    }

    public Board(final int w, final int h, final String backgroundPath, final TerrainMap terrainMap,
                 final String screenName) {
        this.width = w;
        this.height = h;
        this.backgroundPath = backgroundPath;
        this.background = Resources.images().get(backgroundPath);
        this.screenName = screenName;
        this.tiles = new HashMap<>(w * h);
        this.terrainMap = terrainMap;
        tilesUpperLeft = getTilesUpperLeft();
        setupTiles();
        setupResizeListener();
        initialBoardShift();
        highlightMask = SimpleHighlighter.get();
        HexStratLogger.log().log(Level.INFO, "Loaded Board with background: \"{0}\"", this.backgroundPath);
        setupInput();
    }

    public Board(final String name) {
        this((String) null, name);
    }

    public static float getZoom() {
        return zoom;
    }

    public static void setZoom(float zoom) {
        Board.zoom = zoom;
    }

    public TerrainMap getTerrainMap() {
        return terrainMap;
    }

    public void setTerrainMap(final TerrainMap terrainMap) {
        this.terrainMap = terrainMap;
    }

    private void initialBoardShift() {
        move(getBackgroundOffsetPosX() / 2f, 0);
        move(0, getBackgroundOffsetPosY() / 2f);
    }

    private void setupResizeListener() {
        Game.window().onResolutionChanged(r -> updateBoardPosition());
    }

    private void updateBoardPosition() {
        if (Game.window().getHeight() - shiftY >= getZoom() * (background.getHeight() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED)) {
            final float offset = getBackgroundOffsetPosY();
            move(0, offset - shiftY);
        }

        if (Game.window().getWidth() - shiftX >= getZoom() * (background.getWidth() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED)) {
            final float offset = getBackgroundOffsetPosX();
            move(offset - shiftX, 0);
        }
    }

    private float getBackgroundOffsetPosX() {
        return Math.min(0,
                        Game.window().getWidth() - getZoom() * (background.getWidth() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED));
    }

    private float getBackgroundOffsetPosY() {
        return Math.min(0,
                        Game.window().getHeight() - getZoom() * (background.getHeight() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED));
    }

    private Point getTilesUpperLeft() {
        final double topWidth = getZoom() * (Tile.getWidth() - 2 * Tile.getSegmentWidth()); // ----
        final double startX = freeSpaceVertical(
                topWidth) / 2 + getZoom() * (Tile.getWidth() / 2 - 1.33 * Tile.getSegmentWidth()); // drawn
        // centered
        final double startY = getZoom() * (background.getHeight() / 2d * BACKGROUND_ZOOM_FACTOR - height / 4d * Tile.getHeight() + Tile.getHeight() / 2.4);
        return new Point((int) startX, (int) startY);
    }

    private double freeSpaceVertical(final double topWidth) {
        return getZoom() * (background.getWidth() * BACKGROUND_ZOOM_FACTOR
                - (HexMaths.effectiveWidth(width) * Tile.getWidth() + (HexMaths.effectiveWidth(width) - 1) * topWidth));
    }

    public synchronized void setHighlightMask(final IHighlightMask mask) {
        this.highlightMask = mask;
    }

    private void setupInput() {
        Input.mouse().addMouseMotionListener(this);

        InputController.get().onKeyPressed(KeyEvent.VK_W, e -> cameraPanUp(), Set.of(screenName), bInputGroupVert);
        InputController.get().onKeyPressed(KeyEvent.VK_A, e -> cameraPanLeft(), Set.of(screenName), bInputGroupHor);
        InputController.get().onKeyPressed(KeyEvent.VK_S, e -> cameraPanDown(), Set.of(screenName), bInputGroupVert);
        InputController.get().onKeyPressed(KeyEvent.VK_D, e -> cameraPanRight(), Set.of(screenName), bInputGroupHor);
        InputController.get().onKeyTyped(KeyEvent.VK_M, e -> toggleDataView(), Set.of(screenName));
        InputController.get().onKeyTyped(KeyEvent.VK_C, e -> toggleApMpView(), Set.of(screenName));
        InputController.get().onKeyTyped(KeyEvent.VK_F11, e -> showHelp(), Set.of(screenName));
        InputController.get().onWheelMoved(this::doZoom, Set.of(screenName));
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(null,
                                      "Use W, A, S and D to control map movement.\nUse M to toggle map and C to toggle character information, P to toggle Audio.\nUse the mouse wheel for zoom.\nIn the Editor, T may be used to trigger Traps.",
                                      "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doZoom(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0)
            zoomIn();
        else zoomOut();
    }

    private void zoomIn() {
        final float newZoom = Math.min(2.5f, getZoom() + .075f);
        if (newZoom != getZoom()) {
            HexStratLogger.log().log(Level.INFO, "Zoom in: {0}", newZoom);
            updateForZoom(newZoom);
        }
    }

    private void zoomOut() {
        final float newZoom = Math.max(1, getZoom() - .075f);
        if (newZoom != getZoom()) {
            HexStratLogger.log().log(Level.INFO, "Zoom in: {0}", newZoom);
            updateForZoom(newZoom);
        }
    }

    public void resetZoom() {
        if (thisZoom == getZoom()) {
            HexStratLogger.log().info("Reset Zoom to 1f");
            updateForZoom(1f);
        } else {
            HexStratLogger.log().log(Level.INFO, "Custom reset zoom for {0} != {1}", new Object[]{thisZoom, getZoom()});
            thisZoom = 1f;
            float deltaX = (1f - getZoom()) * background.getWidth() * BACKGROUND_ZOOM_FACTOR;
            float deltaY = (1f - getZoom()) * background.getHeight() * BACKGROUND_ZOOM_FACTOR;
            setZoom(1f);
            tiles.values().parallelStream().forEach(t -> {
                t.updateZoom(getZoom());
                t.move(-deltaX / 8, deltaY / 10);
            });
            updateBoardPosition();
            // TODO: fix this
        }
    }

    // yes ... yes... post patches :)
    private synchronized void updateForZoom(float newZoom) {
        final float oldZoom = getZoom();
        setZoom(newZoom);
        thisZoom = newZoom;
        tiles.values().parallelStream().forEach(t -> t.updateZoom(newZoom));
        float deltaX = (newZoom - oldZoom) * background.getWidth() * BACKGROUND_ZOOM_FACTOR;
        float deltaY = (newZoom - oldZoom) * background.getHeight() * BACKGROUND_ZOOM_FACTOR;
        move(-deltaX / 2, -deltaY / 2);
        updateBoardPosition();
    }

    private void toggleDataView() {
        showMapDetails.set(!showMapDetails.get());
    }

    private void toggleApMpView() {
        Creature.showMpAp.set(!Creature.showMpAp.get());
    }

    private synchronized void cameraPanRight() {
        if (Game.window().getWidth() - shiftX < getZoom() * (background.getWidth() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED)) {
            tiles.values().parallelStream().forEach(h -> h.move(-getZoom() * PAN_SPEED, 0));
            shiftX -= getZoom() * PAN_SPEED;
        }
    }

    private synchronized void cameraPanDown() {
        if (Game.window().getHeight() - shiftY < getZoom() * (background.getHeight() * BACKGROUND_ZOOM_FACTOR - PAN_SPEED)) {
            tiles.values().parallelStream().forEach(h -> h.move(0, -getZoom() * PAN_SPEED));
            shiftY -= getZoom() * PAN_SPEED;
        }
    }

    private synchronized void cameraPanLeft() {
        if (shiftX <= getZoom() * -PAN_SPEED) {
            tiles.values().parallelStream().forEach(h -> h.move(getZoom() * PAN_SPEED, 0));
            shiftX += getZoom() * PAN_SPEED;
        }
    }

    private synchronized void cameraPanUp() {
        if (shiftY <= getZoom() * -PAN_SPEED) {
            shiftY += getZoom() * PAN_SPEED;
            tiles.values().parallelStream().forEach(h -> h.move(0, getZoom() * PAN_SPEED));
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
        for (var row = 0; row < height; row++) {
            for (var col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                final double x = tilesUpperLeft.getX() + col * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift;
                final double y = tilesUpperLeft.getY() + row * (0.5 * hexHeight + PADDING);
                tiles.put(new BoardCoordinate(col, row), new Tile(new BoardCoordinate(col, row), (int) x, (int) y,
                                                                  c -> terrainMap.getTerrainAt(c.x, c.y)));
            }
        }
    }


    public synchronized void move(float rx, float ry) {
        final float tx = (shiftX + rx > 0) ? 0 : rx;
        final float ty = (shiftY + ry > 0) ? 0 : ry;
        shiftX += tx;
        shiftY += ty;
        tiles.values().parallelStream().forEach(t -> t.move(tx, ty));
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

    public boolean doesNotHover() {
        return !this.doHover.get();
    }

    public void doNotHover() {
        this.doHover.set(false);
    }

    private void mouseUpdate(final MouseEvent e) {
        highlightTiles.forEach(Tile::clearHover);
        highlightTiles.clear();
        if (!doHover.get()) {
            return;
        }

        final var hoveredTile = findHovered(e);
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

    private boolean updateHighlightingRecursive(final BoardCoordinate hPoint, final boolean[][] hl, final Point anchor,
                                                final Set<Tile> highlightTiles) {
        for (var y = 0; y < hl.length; y++) {
            for (var x = 0; x < hl[y].length; x++) {
                if (hl[y][x] && !processSingleTileHighlight(hPoint, x, y, anchor, highlightTiles))
                    return false;
            }
        }
        return true;
    }

    private boolean processSingleTileHighlight(BoardCoordinate hPoint, int x, int y, Point anchor,
                                               Set<Tile> highlightTiles) {
        // transform target in boardCoordinates
        final var effectiveCoordinate = hPoint.translateRelativeX(x - anchor.x, y - anchor.y);
        final var targetTile = getTile(effectiveCoordinate);
        // invalid as too close to border
        if (targetTile == null)
            return false;

        final var targetTerrainType = targetTile.getTerrainType();

        if (highlightTiles.add(targetTile)) {
            final var recHighlightMask = new ImprintHighlighter(targetTerrainType.getNode().getImprint());
            return updateHighlightingRecursive(targetTile.getCoordinate(), recHighlightMask.getGrid(),
                                               targetTerrainType.getNode().getPos(), highlightTiles);
        }
        return true;
    }

    @Override
    public void render(final Graphics2D g) {
        ImageRenderer.renderScaled(g, background, shiftX, shiftY, getZoom() * BACKGROUND_ZOOM_FACTOR,
                                   getZoom() * BACKGROUND_ZOOM_FACTOR);
        for (final Tile tile : tiles.values())
            tile.render(g, showMapDetails.get());

        for (var row = 0; row < height; row++) {
            for (var col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                tiles.get(new BoardCoordinate(col, row)).renderDecorations(g);
            }
        }
    }

    public void jointRender(final Graphics2D g, PlayerId renderOwner, CreatureFactory factory, TrapSpawner traps) {
        ImageRenderer.renderScaled(g, background, shiftX, shiftY, getZoom() * BACKGROUND_ZOOM_FACTOR,
                                   getZoom() * BACKGROUND_ZOOM_FACTOR);
        for (final Tile tile : tiles.values())
            tile.render(g, showMapDetails.get());

        for (var row = 0; row < height; row++) {
            for (var col = 0; col < HexMaths.effectiveWidth(width) - lineToggle(row); col++) {
                final var coordinate = new BoardCoordinate(col, row);
                traps.getRoot(coordinate).ifPresent(t -> t.renderBaseFor(g, renderOwner));
                tiles.get(coordinate).renderDecorations(g);
                factory.get(coordinate).ifPresent(c -> c.render(g));
                traps.getRoot(coordinate).ifPresent(t -> t.renderTriggerFor(g, renderOwner));
            }
        }
        final var str = "F11 for help.";
        g.setFont(Main.TEXT_NORMAL);
        g.setColor(Color.YELLOW);
        TextRenderer.render(g, str, Game.window().getWidth() - TextRenderer.getWidth(g, str) - 15,
                            Game.window().getHeight() - TextRenderer.getHeight(g, str) - 25);
    }

}
