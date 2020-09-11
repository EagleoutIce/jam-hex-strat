package de.flojo.jam.screens;

import java.awt.Graphics2D;

import de.flojo.jam.game.Board;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class IngameScreen extends Screen {

    private Board board;

    public static final String NAME = "INGAME";

    public IngameScreen() {
        super(NAME);
        Game.log().info("Building Ingame Screen");
    }

    @Override
    public void prepare() {
        super.prepare();

        board = new Board(24, 33, "Rcihtiges Hexfeld Vorlage 0.1.png", "configs/default.terrain");
    }


    @Override
    public void render(final Graphics2D g) {
        board.render(g);
        super.render(g);
    }

}
