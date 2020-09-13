package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class IngameScreen extends Screen {

    private Board board;
    private CreatureFactory creatureFactory;
    private TrapSpawner trapSpawner;
    private PlayerId ourId;

    public static final String NAME = "INGAME";

    private IngameScreen() {
        super(NAME);
        Game.log().info("Building Ingame Screen");
    }
    
    private static final IngameScreen instance = new IngameScreen();

    public static IngameScreen get() {
        return instance;
    }


    public void setup(PlayerId ourId) {
        board = new Board(new TerrainMap(ConnectScreen.get().getClientController().getContext().getTerrain()), IngameScreen.NAME);
        trapSpawner = new TrapSpawner(board, IngameScreen.NAME);
        creatureFactory = new CreatureFactory(IngameScreen.NAME, trapSpawner.getTraps());
        this.ourId = ourId;
    }

    @Override
    public void prepare() {
        super.prepare();

    }

    @Override
    public void render(final Graphics2D g) {
        if(board != null)
            board.jointRender(g, ourId, creatureFactory, trapSpawner);
        g.setPaint(Color.MAGENTA);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.renderWithLinebreaks(g, "Selection: " + creatureFactory.getSelectedCreature(), Main.INNER_MARGIN, 90d, Game.window().getWidth() - 2*Main.INNER_MARGIN);
        super.render(g);
    }

}
