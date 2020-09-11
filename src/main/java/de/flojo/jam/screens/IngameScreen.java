package de.flojo.jam.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.FreeFlightCamera;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

public class IngameScreen extends Screen {
    public static final String NAME = "INGAME";

    List<Hexagon> hexxagons = new LinkedList<>();

    public IngameScreen() {
        super(NAME);

    }

    private int lineToggle(int row) {
        return (row + 1) % 2;
    }

    private void drawHexGrid(Point2D upperLeft, int width, int height, int padding) {
        final int radius = 30;
        final double hexWidth = Hexagon.getWidthOf(radius);
        final double hexHeight = Hexagon.getHeightOf(radius);
        final double hexSeg = Hexagon.getSegmentWidthOf(radius);
        final double rowShift = hexWidth - hexSeg + padding;
        final double hexMidWidth = hexWidth - 2 * hexSeg;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < Math.ceil(width / 2d) - lineToggle(row); col++) {
                int x = (int) (upperLeft.getX() + col * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift);
                int y = (int) (upperLeft.getY() + row * (0.5 * hexHeight + padding));
                drawHex(x, y, radius);
            }
        }
    }

    private String coord(int value) {
        return (value > 0 ? "+" : "") + Integer.toString(value);
    }

    @Override
    public void prepare() {
        super.prepare();

        Input.keyboard().onKeyPressed(KeyEvent.VK_W, e -> {
            hexxagons.forEach(h -> h.move(0, 5));
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_A, e -> {
            hexxagons.forEach(h -> h.move(5, 0));
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_S, e -> {
            hexxagons.forEach(h -> h.move(0, -5));
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_D, e -> {
            hexxagons.forEach(h -> h.move(-5, 0));
        });

        Input.keyboard().onKeyPressed(KeyEvent.VK_Q, e -> {
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_D, e -> {
            hexxagons.forEach(h -> h.move(-5, 0));
        });

        Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (this.isVisible()) {
                System.exit(0);
            }
        });
        drawHexGrid(Game.window().getCenter(), 24, 33, 0);
        Game.world().setCamera(new FreeFlightCamera());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        int mx = e.getX();
        int my = e.getY();
        for (Hexagon hexagon : hexxagons) {
            if (hexagon.contains(mx, my)) {
                hexagon.hover.set(true);
            } else {
                hexagon.hover.set(false);
            }
        }
    }

    private void drawHex(int x, int y, int r) {
        Hexagon hex = new Hexagon(x, y, r);
        hexxagons.add(hex);
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }
        for (var hex : hexxagons) {

            if (hex.hover.get()) {
                hex.draw(g, 0, 0x038844, false);
            } else {
                hex.draw(g, 0, 0x008844, true);
            }
            hex.draw(g, 4, 0xFFDD88, false);
        }

    }

}
