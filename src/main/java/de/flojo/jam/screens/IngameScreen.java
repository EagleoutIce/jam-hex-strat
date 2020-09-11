package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import de.flojo.jam.graphics.Hexagon;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class IngameScreen extends Screen {
    private static final BufferedImage background = Resources.images().get("Rcihtiges Hexfeld Vorlage 0.1.png");
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
                int x = (int) (upperLeft.getX() + col * (hexWidth + hexMidWidth) + lineToggle(row) * rowShift) + 200;
                int y = (int) (upperLeft.getY() + row * (0.5 * hexHeight + padding)) + 100;
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
            if(by <=-5){
                by += 5;
                hexagons.forEach(h -> h.move(0, 5));
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_A, e -> {
            if(bx <= -5){
                hexagons.forEach(h -> h.move(5, 0));
                bx += 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_S, e -> {
            if(Game.window().getHeight() - by  < background.getHeight() - 5){
                hexagons.forEach(h -> h.move(0, -5));
                by -= 5;
            }
        });
        Input.keyboard().onKeyPressed(KeyEvent.VK_D, e -> {
            if(Game.window().getWidth() - bx  < background.getWidth() - 5){
                hexagons.forEach(h -> h.move(-5, 0));
                bx -= 5;
            }
        });

        Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (this.isVisible()) {
                System.exit(0);
            }
        });
        drawHexGrid(new Point(0,0), 24, 33, 0);
    }

    private int mx;
    private int my;

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        mx = e.getX();
        my = e.getY();
        boolean hovered = false;
        for (Hexagon hexagon : hexagons) {
            if (!hovered && hexagon.contains(mx, my)) {
                hexagon.hover.set(true);
                hovered = true;
            } else {
                hexagon.hover.set(false);
            }
        }
    }

    private void drawHex(int x, int y, int r) {
        Hexagon hex = new Hexagon(x, y, r);
        hexagons.add(hex);
    }

    @Override
    public void render(final Graphics2D g) {
        ImageRenderer.render(g, background, bx, by);

        for (var hex : hexagons) {


            if (hex.hover.get()) {
                hex.draw(g, 0, new Color(0.6f,0.6f,0.3f,0.2f), true);
            }
            hex.draw(g, 4, new Color(0.4f,0.6f,0.3f,0.6f), false);

        }
    }

}
