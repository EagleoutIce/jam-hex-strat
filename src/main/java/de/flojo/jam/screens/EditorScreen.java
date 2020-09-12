package de.flojo.jam.screens;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.Architect;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.graphics.ImageButton;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

public class EditorScreen extends Screen {

    private Board board;
    private Architect architect;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static final String NAME = "EDITOR";
    private static final String TERRAIN_SUFFIX = ".terrain";

    private TerrainId currentTerrain = TerrainId.T_EMPTY;

    private Button newField;
    private Button saveField;
    private Button loadField;
    private TextFieldComponent terrainName;

    private List<ImageButton> terrainButtons;

    public EditorScreen() {
        super(NAME);
        Game.log().info("Building Editor Screen");
    }

    @Override
    public void prepare() {
        super.prepare();

        board = new Board(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Main.FIELD_BACKGROUND,
                "configs/empty.terrain");
        architect = new Architect(board);

        Game.window().onResolutionChanged(r -> {
            updateButtonPositions();
        });

        Input.mouse().onDragged(this::plantTile);
        Input.mouse().onClicked(this::plantTile);
        Input.mouse().onMoved(this::lockOnMoved);
        architect.clearField();// init
        terrainName.setText("Pain Terrain Name");
    }

    // TODO: delete with right key

    private void lockOnMoved(MouseEvent c) {
        if(intersectsWithButton(c.getPoint())) {
            board.doNotHover();
        } else {
            board.doHover();
        }
    }

    private boolean intersectsWithButton(Point p) {
        if (newField.getBoundingBox().contains(p) || saveField.getBoundingBox().contains(p)
                || loadField.getBoundingBox().contains(p)) {
            return true;
        }
        for (ImageButton imageButton : terrainButtons) {
            if (imageButton.getBoundingBox().contains(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean bitHigh(int value, int b) {
        return ((value >>> b) & 1) != 0;
    }

    private void plantTile(MouseEvent c) {
        Point p = c.getPoint();
        if (intersectsWithButton(p))
            return;
        Tile t = board.findTile(p);
        if (c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (t != null)
                architect.placeImprint(t.getCoordinate(), currentTerrain.getImprint());
        } else if (c.getButton() == MouseEvent.BUTTON3 || bitHigh(c.getModifiersEx(), 12)) {
            architect.deleteImprint(t.getCoordinate(), currentTerrain.getImprint());
        }
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        initTerrainButtons();
        initFileOperationButtons();
        terrainName = new TextFieldComponent(0, 0, 200d, 40d, "Pain Terrain Name");
        this.getComponents().add(terrainName);
    }

    private void initFileOperationButtons() {
        // TODO: maybe clear/save as?
        newField = new Button("New", Main.GUI_FONT_SMALL);
        newField.onClicked(c -> architect.clearField());
        saveField = new Button("Save", Main.GUI_FONT_SMALL);
        saveField.onClicked(c -> saveField());
        loadField = new Button("Load", Main.GUI_FONT_SMALL);
        loadField.onClicked(c -> loadField());
        updateButtonPositions();
        this.getComponents().add(newField);
        this.getComponents().add(saveField);
        this.getComponents().add(loadField);
    }

    private void saveField() {
        board.getTerrainMap().changeName(terrainName.getText());
        final String chosen = getSaveFile();
        if (chosen == null) {
            Game.log().info("Save was cancelled.");
            return;
        }
        Game.log().log(Level.INFO, "Saving to: \"{0}\"", chosen);
        try (PrintWriter writer = new PrintWriter(new File(chosen))) {
            writer.println(gson.toJson(board.getTerrainMap().getTerrain()));
        } catch (IOException ex) {
            Game.log().warning(ex.getMessage());
        }
    }

    private void loadField() {
        final String chosen = getLoadFile();
        if (chosen == null) {
            Game.log().info("Load was cancelled.");
            return;
        }
        Game.log().log(Level.INFO, "Loading from: \"{0}\"", chosen);
        try {
            TerrainMap map = new TerrainMap(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, new FileInputStream(new File(chosen)),
                    chosen);
            this.board.setTerrainMap(map);
            Game.log().log(Level.INFO, "Loaded Terrain: \"{0}\"", board.getTerrainMap().getTerrain().getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            
        }
        terrainName.setText(board.getTerrainMap().getTerrain().getName());
    }

    private String getLoadFile() {
        final FileDialog loadDialog = new FileDialog(new Frame(), "Save Map", FileDialog.LOAD);
        loadDialog.setFilenameFilter((dir, name) -> name.endsWith(TERRAIN_SUFFIX));
        loadDialog.setAlwaysOnTop(true);
        loadDialog.setMultipleMode(false);
        loadDialog.setVisible(true);
        return loadDialog.getFile() == null ? null
                : Paths.get(loadDialog.getDirectory(), loadDialog.getFile()).toAbsolutePath().toString();
    }



    private String getSaveFile() {
        final FileDialog saveDialog = new FileDialog(new Frame(), "Save Map", FileDialog.SAVE);
        saveDialog.setFilenameFilter((dir, name) -> name.endsWith(TERRAIN_SUFFIX));
        saveDialog.setAlwaysOnTop(true);
        saveDialog.setMultipleMode(false);
        saveDialog.setFile(board.getTerrainMap().getTerrain().getName() + TERRAIN_SUFFIX);
        saveDialog.setVisible(true);
        return saveDialog.getFile() == null ? null
                : Paths.get(saveDialog.getDirectory(), saveDialog.getFile()).toAbsolutePath().toString();
    }

    private void updateButtonPositions() {
        newField.setLocation(Main.LEFT_WIN_OFFSET, Game.window().getHeight() - 90d);
        saveField.setLocation(Main.LEFT_WIN_OFFSET + newField.getWidth() + 10d, Game.window().getHeight() - 90d);
        loadField.setLocation(Main.LEFT_WIN_OFFSET + newField.getWidth() + saveField.getWidth() + 20d, Game.window().getHeight() - 90d);
    }

    private void initTerrainButtons() {
        terrainButtons = new ArrayList<>();
        TerrainId[] terrains = TerrainId.values();
        for (int i = 0; i < terrains.length; i++) {
            TerrainId terrain = terrains[i];
            
            ImageButton imgBt = new ImageButton(310d, 30d, Main.LEFT_WIN_OFFSET, (i + 1) * 45d,
                    terrain.getImprint().getBitMap(), terrain.getName(), Main.TEXT_NORMAL);
            terrainButtons.add(imgBt);
            imgBt.onClicked(c -> {
                this.currentTerrain = terrain;
                TerrainImprint imprint = terrain.getImprint();
                if (imprint.hasBaseResource()) {
                    Game.window().cursor().setVisible(true);
                    Game.window().cursor().set(terrain.getImprint().getBaseResource());
                    // TODO: maybe make more efficient?
                    board.setHighlightMask(new ImprintHighlighter(imprint));
                    Game.window().cursor().showDefaultCursor();
                } else {
                    Game.window().cursor().set(Main.DEFAULT_CURSOR);
                    board.setHighlightMask(SimpleHighlighter.get());
                }
            });
            this.getComponents().add(imgBt);
        }
    }

    @Override
    public void render(final Graphics2D g) {
        board.render(g);

        super.render(g);
    }

}
