package de.flojo.jam.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import de.flojo.jam.game.Board;
import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class IngameScreen extends Screen {
    private static final BufferedImage background = Resources.images().get("Rcihtiges Hexfeld Vorlage 0.1.png");

    Board board;

    int bx = 0, by=0;
    public static final String NAME = "INGAME";

    List<Hexagon> hexagons = new LinkedList<>();
    public IngameScreen() {
        super(NAME);
        Game.window().onResolutionChanged(r -> {
            if(Game.window().getHeight() - by  >= background.getHeight() - 5) {
                int offset = Math.min(0, Game.window().getHeight() - background.getHeight() - 5);
                int rof = by - offset;
                by = offset;
                hexagons.forEach(h -> h.move(0, -rof));
            }
            
            if(Game.window().getWidth() - bx  >= background.getWidth() - 5) { 
                int offset = Math.min(0, Game.window().getWidth() - background.getWidth() - 5);
                bx = offset;
                int rof = bx - offset;
                hexagons.forEach(h -> h.move(-rof, 0));
            }
            }
        );
    }

    @Override
    public void prepare() {
        super.prepare();
        Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (this.isVisible()) {
                System.exit(0);
            }
        });
        board = new Board(24, 33, "Rcihtiges Hexfeld Vorlage 0.1.png");
    }


    @Override
    public void render(final Graphics2D g) {
        board.render(g);
    }

}
