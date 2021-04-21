package de.flojo.jam.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.Architect;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapImprint;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.ISummonCreature;
import de.flojo.jam.game.creature.skills.SkillsPresenter;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.graphics.ImageButton;
import de.flojo.jam.util.FileHelper;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EditorScreen extends Screen {

    public static final String NAME = "EDITOR";
    private final Board board;
    private final CreatureFactory creatureFactory;
    private final TrapSpawner trapSpawner;
    private final SkillsPresenter presenter;
    private final Architect architect;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean showP1 = true;
    private boolean showP2 = true;
    private TerrainId currentTerrain = TerrainId.T_EMPTY;
    private ISummonCreature currentCreature = null;
    private TrapId currentTrapId = null;
    private EditorSelectionMode selectionMode = EditorSelectionMode.TERRAIN;
    private Button newField;
    private Button saveField;
    private Button loadField;
    private Button p1;
    private Button p2;
    private Button both;
    private Button nextRound;
    private TextFieldComponent terrainName;
    private List<ImageButton> terrainButtons;
    private List<ImageButton> trapButtons;
    private List<Button> creatureButtons;
    private boolean locked;

    public EditorScreen() {
        super(NAME);

        board = new Board("configs/empty.terrain", EditorScreen.NAME);
        trapSpawner = new TrapSpawner(board, EditorScreen.NAME);
        creatureFactory = new CreatureFactory(EditorScreen.NAME, board, trapSpawner.getTraps());
        architect = new Architect(board, this.creatureFactory, this.trapSpawner);

        Game.log().info("Building Editor Screen");
        Game.window().onResolutionChanged(r -> updatePositions());

        Input.mouse().onDragged(this::plantTileOrOther);
        Input.mouse().onClicked(this::plantTileOrOther);
        Input.mouse().onMoved(this::lockOnMoved);

        InputController.get().onKeyPressed(KeyEvent.VK_T, c -> {
            if (trapSpawner.getSelectedTrap() != null) {
                trapSpawner.getSelectedTrap().trigger();
            }
        }, EditorScreen.NAME);

        InputController.get().onKeyPressed(KeyEvent.VK_K, c -> {
            if (creatureFactory.getSelectedCreature() != null) {
                creatureFactory.getSelectedCreature().die();
            }
        }, EditorScreen.NAME);

        InputController.get().onKeyPressed(KeyEvent.VK_ESCAPE, e -> changeScreen(MenuScreen.NAME), EditorScreen.NAME);
        presenter = new SkillsPresenter(this, board, creatureFactory, trapSpawner, getFakeId(), EditorScreen.NAME);
        presenter.enable();
    }

    @Override
    public void prepare() {
        super.prepare();

        // TODO: maybe group reset?
        trapSpawner.removeAll();
        creatureFactory.removeAll();
        architect.clearField();// init

        this.selectionMode = EditorSelectionMode.TERRAIN;
        this.currentTerrain = TerrainId.T_EMPTY;

        Game.loop().perform(100, this::updatePositions);

        terrainName.setText("Pain Terrain Name");
    }

    private void lockOnMoved(MouseEvent c) {
        if (intersectsWithButton(c.getPoint())) {
            board.doNotHover();
        } else {
            board.doHover();
        }
    }

    // TODO: simplify
    private boolean intersectsWithButton(Point p) {
        // this did escalate... maybe with list?
        if (newField.getBoundingBox().contains(p) || saveField.getBoundingBox().contains(p)
                || loadField.getBoundingBox().contains(p) || p1.getBoundingBox().contains(p)
                || p2.getBoundingBox().contains(p) || both.getBoundingBox().contains(p)
                || nextRound.getBoundingBox().contains(p)) {
            return true;
        }
        for (ImageButton imageButton : terrainButtons) {
            if (imageButton.getBoundingBox().contains(p))
                return true;
        }
        for (Button button : creatureButtons) {
            if (button.getBoundingBox().contains(p))
                return true;
        }
        for (ImageButton button : trapButtons) {
            if (button.getBoundingBox().contains(p))
                return true;
        }
        return false;
    }

    private boolean bitHigh(int value, int b) {
        return ((value >>> b) & 1) != 0;
    }

    private void plantTile(MouseEvent c) {
        if (this.selectionMode != EditorSelectionMode.TERRAIN || architect == null) {
            return;
        }

        if (currentTerrain == null || currentTerrain == TerrainId.T_EMPTY)
            return;

        Point p = c.getPoint();
        if (intersectsWithButton(p))
            return;
        Tile t = board.findTile(p);
        if (t == null)
            return;

        if (c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
            architect.placeImprint(t.getCoordinate(), currentTerrain.getImprint());
        else if (c.getButton() == MouseEvent.BUTTON3 || bitHigh(c.getModifiersEx(), 12))
            architect.deleteImprint(t.getCoordinate(), currentTerrain.getImprint());
    }

    private void spawnTrap(MouseEvent c) {
        if (this.selectionMode != EditorSelectionMode.TRAP) {
            return;
        }
        Point p = c.getPoint();
        if (intersectsWithButton(p))
            return;
        Tile t = board.findTile(p);
        if (t == null)
            return;
        if (c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (t.getTerrainType() == TerrainTile.EMPTY && creatureFactory.get(t.getCoordinate()).isEmpty()
                    && trapSpawner.canBePlaced(creatureFactory, currentTrapId, t, getFakeId(), board)) {
                trapSpawner.spawnTrap(currentTrapId, getFakeId(), t);
            }
        } else if (c.getButton() == MouseEvent.BUTTON3 || bitHigh(c.getModifiersEx(), 12)) {
            trapSpawner.removeTrap(t);
        }
    }

    private void summonCreature(MouseEvent c) {
        if (this.selectionMode != EditorSelectionMode.CREATURE)
            return;

        Point p = c.getPoint();
        if (intersectsWithButton(p))
            return;
        Tile t = board.findTile(p);
        if (t == null)
            return;
        if (c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (!t.getTerrainType().blocksWalking() && (getFakeId() == null || getFakeId() == t.getPlacementOwner()) && trapSpawner.get(t.getCoordinate()).isEmpty()
                    && creatureFactory.get(t.getCoordinate()).isEmpty()) {
                currentCreature.summon(UUID.randomUUID().toString(), t);
            }
        } else if (c.getButton() == MouseEvent.BUTTON3 || bitHigh(c.getModifiersEx(), 12)) {
            creatureFactory.removeCreature(t);
        }
    }

    void plantTileOrOther(MouseEvent e) {
        if (!board.doesHover())
            return;
        switch (selectionMode) {
            case CREATURE:
                summonCreature(e);
                break;
            case TERRAIN:
                plantTile(e);
                break;
            case TRAP:
                spawnTrap(e);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        initTerrainButtons();
        initTrapButtons();
        initCreatureButtons();
        initFileOperationButtons();
        terrainName = new TextFieldComponent(0, 0, 200d, 40d, "Pain Terrain Name");
        this.getComponents().add(terrainName);
    }

    private void initFileOperationButtons() {
        newField = new Button("New", Main.GUI_FONT_SMALL);
        newField.onClicked(c -> {
            architect.clearField();
            creatureFactory.removeAll();
            trapSpawner.removeAll();
        });
        saveField = new Button("Save", Main.GUI_FONT_SMALL);
        saveField.onClicked(c -> saveField());
        loadField = new Button("Load", Main.GUI_FONT_SMALL);
        loadField.onClicked(c -> loadField());
        p1 = new Button("P1", Main.GUI_FONT_SMALL);
        p1.onClicked(c -> {//
            showP1 = true;
            showP2 = false;
            presenter.setPlayerId(PlayerId.ONE);
            architect.setPlayerId(PlayerId.ONE);
            p1.setColors(Color.GREEN, Color.GREEN.brighter());
            p2.setColors(Color.WHITE, Color.WHITE.darker());
            both.setColors(Color.WHITE, Color.WHITE.darker());
        });
        p2 = new Button("P2", Main.GUI_FONT_SMALL);
        p2.onClicked(c -> {//
            showP1 = false;
            showP2 = true;
            presenter.setPlayerId(PlayerId.TWO);
            architect.setPlayerId(PlayerId.TWO);
            p1.setColors(Color.WHITE, Color.WHITE.darker());
            p2.setColors(Color.GREEN, Color.GREEN.brighter());
            both.setColors(Color.WHITE, Color.WHITE.darker());
        });
        both = new Button("Both", Main.GUI_FONT_SMALL);
        both.onClicked(c -> {
            showP1 = true;
            showP2 = true;
            presenter.setPlayerId(null);
            architect.setPlayerId(null);
            p1.setColors(Color.WHITE, Color.WHITE.darker());
            p2.setColors(Color.WHITE, Color.WHITE.darker());
            both.setColors(Color.GREEN, Color.GREEN.brighter());
        });
        both.setColors(Color.GREEN, Color.GREEN.brighter());

        // TODO: show round counter
        nextRound = new Button("Next Round", Main.TEXT_STATUS);
        nextRound.onClicked(c -> {
            creatureFactory.resetAll();
            presenter.update();
        });

        updatePositions();
        this.getComponents().add(newField);
        this.getComponents().add(saveField);
        this.getComponents().add(loadField);
        this.getComponents().add(p1);
        this.getComponents().add(p2);
        this.getComponents().add(both);
        this.getComponents().add(nextRound);
    }

    private void saveField() {
        board.getTerrainMap().changeName(terrainName.getText());
        final String chosen = FileHelper.askForTerrainPathSave(board.getTerrainMap().getTerrain().getName());
        if (chosen == null) {
            Game.log().info("Save was cancelled.");
            return;
        }
        HexStratLogger.log().log(Level.INFO, "Saving to: \"{0}\"", chosen);
        try (PrintWriter writer = new PrintWriter(chosen)) {
            writer.println(gson.toJson(board.getTerrainMap().getTerrain()));
        } catch (IOException ex) {
            Game.log().warning(ex.getMessage());
        }
    }

    private void loadField() {
        final String chosen = FileHelper.askForTerrainPathLoad();
        if (chosen == null) {
            Game.log().info("Load was cancelled.");
            return;
        }
        HexStratLogger.log().log(Level.INFO, "Loading from: \"{0}\"", chosen);
        try {
            TerrainMap map = new TerrainMap(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, new FileInputStream(chosen),
                    chosen);
            this.board.setTerrainMap(map);
            HexStratLogger.log().log(Level.INFO, "Loaded Terrain: \"{0}\"", board.getTerrainMap().getTerrain().getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        terrainName.setText(board.getTerrainMap().getTerrain().getName());
        // after load overlay :D
        creatureFactory.removeAll();
        trapSpawner.removeAll();
    }

    private void updatePositions() {
        newField.setLocation(Main.INNER_MARGIN, Game.window().getHeight() - 90d);
        saveField.setLocation(Main.INNER_MARGIN + newField.getWidth() + 10d, Game.window().getHeight() - 90d);
        loadField.setLocation(Main.INNER_MARGIN + newField.getWidth() + saveField.getWidth() + 20d,
                Game.window().getHeight() - 90d);
        int width = Game.window().getWidth();
        p1.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - p2.getWidth() - both.getWidth() - 35d, 18d);
        p2.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - both.getWidth() - 30d, 18d);
        both.setLocation(width - Main.INNER_MARGIN - both.getWidth() - 10d, 18d);
        nextRound.setLocation(width - Main.INNER_MARGIN - nextRound.getWidth() - 10d, 30d + both.getHeight());
        for (int i = 0; i < trapButtons.size(); i++) {
            trapButtons.get(i).setLocation(width - 260d - Main.INNER_MARGIN - 10d, (i + 3) * 45d + 15d);
        }
    }

    private void initCreatureButtons() {
        creatureButtons = new ArrayList<>();
        CreatureId[] creatures = CreatureId.values();
        for (int i = 0; i < creatures.length; i++) {
            CreatureId creatureId = creatures[i];
            if (creatureId == CreatureId.NONE)
                continue;
            instantiateButton(creatureId, true, i);
            instantiateButton(creatureId, false, i);
        }
    }

    private void instantiateButton(CreatureId creatureId, boolean p1, int i) {
        Button bt = new Button(creatureId.getName() + (p1 ? "-P1" : "-P2"), Main.TEXT_NORMAL, 20);
        bt.setLocation(Main.INNER_MARGIN + (p1 ? 0 : 125d), (TerrainId.values().length + 1 + i) * 45d + 15d);
        creatureButtons.add(bt);
        bt.onClicked(c -> {
            this.currentTerrain = null;
            this.selectionMode = EditorSelectionMode.CREATURE;
            this.currentTrapId = null;

            this.currentCreature = (n, t) -> creatureFactory.getSpell(creatureId).summon(creatureId + "_" + n, t,
                    p1 ? PlayerId.ONE : PlayerId.TWO, true);
            BufferedImage img = creatureFactory.getBufferedImage(creatureId, p1);
            if (img != null) {
                Game.window().cursor().setVisible(true);
                Game.window().cursor().set(img);
                board.setHighlightMask(SimpleHighlighter.get());
                Game.window().cursor().showDefaultCursor();
            } else {
                resetCursor();
            }
        });
        this.getComponents().add(bt);
    }

    private void resetCursor() {
        Game.window().cursor().set(Main.DEFAULT_CURSOR);
        board.setHighlightMask(SimpleHighlighter.get());
    }

    private void initTrapButtons() {
        trapButtons = new ArrayList<>();
        TrapId[] traps = TrapId.values();
        final int width = Game.window().getWidth();
        for (int i = 0; i < traps.length; i++) {
            TrapId t = traps[i];

            ImageButton imgBt = new ImageButton(260d, 30d, width - 260d, (i + 3) * 45d, t.getImprint().getBitMap(),
                    t.getName(), Main.TEXT_NORMAL);
            imgBt.setTextAlign(Align.RIGHT);
            trapButtons.add(imgBt);
            imgBt.onClicked(c -> {
                this.currentCreature = null;
                this.currentTrapId = t;
                this.selectionMode = EditorSelectionMode.TRAP;

                TrapImprint imprint = t.getImprint();
                Game.window().cursor().setVisible(true);
                Game.window().cursor().set(t.getImprint().getNormalRenderer().getImage());
                // TODO: maybe make more efficient?
                board.setHighlightMask(new ImprintHighlighter(imprint));
                Game.window().cursor().showDefaultCursor();
            });
            this.getComponents().add(imgBt);
        }
    }

    private void initTerrainButtons() {
        terrainButtons = new ArrayList<>();
        TerrainId[] terrains = TerrainId.values();
        for (int i = 0; i < terrains.length; i++) {
            TerrainId t = terrains[i];

            ImageButton imgBt = new ImageButton(260d, 30d, Main.INNER_MARGIN, (i + 1) * 45d, t.getImprint().getBitMap(),
                    t.getName(), Main.TEXT_NORMAL);
            terrainButtons.add(imgBt);
            imgBt.onClicked(c -> {
                this.currentCreature = null;
                this.currentTerrain = t;
                this.selectionMode = EditorSelectionMode.TERRAIN;

                TerrainImprint imprint = t.getImprint();
                if (imprint.hasBaseResource()) {
                    Game.window().cursor().setVisible(true);
                    Game.window().cursor().set(t.getImprint().getBaseResource());
                    // TODO: maybe make more efficient?
                    board.setHighlightMask(new ImprintHighlighter(imprint));
                    Game.window().cursor().showDefaultCursor();
                } else {
                    resetCursor();
                }
            });
            this.getComponents().add(imgBt);
        }
    }

    private PlayerId getFakeId() {
        if (showP1 && showP2)
            return null;
        return showP1 ? PlayerId.ONE : PlayerId.TWO;
    }

    @Override
    public void render(final Graphics2D g) {
        board.jointRender(g, getFakeId(), creatureFactory, trapSpawner);
        super.render(g);
    }

    private void changeScreen(final String name) {
        if (this.locked)
            return;

        Game.window().cursor().set(Main.DEFAULT_CURSOR);

        this.locked = true;
        Game.window().getRenderComponent().fadeOut(650);
        Game.loop().perform(950, () -> {
            Game.screens().display(name);
            Game.window().getRenderComponent().fadeIn(650);
            this.locked = false;
        });
    }


    private enum EditorSelectionMode {
        TERRAIN, CREATURE, TRAP
    }

}
