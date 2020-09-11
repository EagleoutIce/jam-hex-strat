package de.flojo.jam.screens;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import de.flojo.jam.Main;
import de.flojo.jam.game.Board;
import de.flojo.jam.game.terrain.management.TerrainId;
import de.flojo.jam.game.terrain.management.TerrainImprint;
import de.flojo.jam.graphics.ImageButton;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class EditorScreen extends Screen {
    private Board board;

    public static final String NAME = "EDITOR";

    private TerrainId currentTerrain = TerrainId.T_EMPTY;

    private List<ImageButton> terrainButtons;
    public EditorScreen() {
        super(NAME);
        Game.log().info("Building Editor Screen");
    }

    @Override
    public void prepare() {
        super.prepare();

        board = new Board(24, 33, "Rcihtiges Hexfeld Vorlage 0.1.png", "configs/default.terrain");
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        terrainButtons = new ArrayList<>();
        TerrainId[] terrains = TerrainId.values();
        for (int i = 0; i < terrains.length; i++) {
            TerrainId terrain = terrains[i];
            ImageButton imgBt = new ImageButton(310d, 30d, 20d, (i + 1) * 45d,terrain.getImprint().getBitMap(), terrain.getName(), Main.TEXT_NORMAL);
            terrainButtons.add(imgBt);
            imgBt.onClicked(c -> {
                this.currentTerrain = terrain;
                TerrainImprint imprint = terrain.getImprint();
                if(imprint.hasBaseResource()) {
                    Game.window().cursor().setVisible(true);
                    Game.window().cursor().set(terrain.getImprint().getBaseResource());
                } else {
                    Game.window().cursor().setVisible(false);
                }
                Game.window().cursor().showDefaultCursor();
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
