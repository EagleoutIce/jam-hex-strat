package de.flojo.jam.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import de.flojo.jam.game.Board;
import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

public class EditorScreen extends Screen {
    private boolean locked;

    Board board;

    int bx = 0, by=0;
    public static final String NAME = "INGAME";

    List<Hexagon> hexagons = new LinkedList<>();
    public EditorScreen() {
        super(NAME);
    }

    @Override
    public void prepare() {
        super.prepare();
        Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (this.locked) {
                return;
            }

            if (this.isVisible()) {
                this.locked = true;
                Game.window().getRenderComponent().fadeOut(1000);
                Game.loop().perform(1500, () -> {
                    Game.screens().display(MenuScreen.NAME);
                    Game.window().getRenderComponent().fadeIn(1000);
                    this.locked = false;
                });
            }
        });
        board = new Board(24, 33, "Rcihtiges Hexfeld Vorlage 0.1.png", "configs/default.terrain");
    }


    @Override
    public void render(final Graphics2D g) {
        board.render(g);
    }

}
