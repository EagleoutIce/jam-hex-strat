package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.creature.CreatureFactory;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class IngameScreen extends Screen {

    private Board board;
    private CreatureFactory summoner;

    public static final String NAME = "INGAME";

    private IngameScreen() {
        super(NAME);
        Game.log().info("Building Ingame Screen");
    }
    
    private static final IngameScreen instance = new IngameScreen();
    public static IngameScreen get() {
        return instance;
    }


    public void setup() {
        board = new Board(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Main.FIELD_BACKGROUND, new TerrainMap(ConnectScreen.get().getClientController().getContext().getTerrain()));
    }

    @Override
    public void prepare() {
        super.prepare();

        // TODO: make more beautiful?
        summoner = new CreatureFactory();
    }

    @Override
    public void render(final Graphics2D g) {
        if(board != null)
            board.render(g);
        summoner.render(g);
        g.setPaint(Color.MAGENTA);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.renderWithLinebreaks(g, "Selection: " + summoner.getSelectedCreature(), Main.INNER_MARGIN, 90d, Game.window().getWidth() - 2*Main.INNER_MARGIN);
        super.render(g);
    }

}
