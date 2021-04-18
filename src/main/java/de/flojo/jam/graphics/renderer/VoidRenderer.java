package de.flojo.jam.graphics.renderer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class VoidRenderer implements IRenderData {

    private static VoidRenderer instance = new VoidRenderer();

    private VoidRenderer() {
    }

    public static VoidRenderer get() {
        return instance;
    }

    @Override
    public void render(Graphics2D g, Point2D pos, RenderHint... ignored) {
        // do nothing
    }

    @Override
    public boolean hasImage() {
        return false;
    }

    @Override
    public BufferedImage getImage() {
        return null;
    }

    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle((int) pos.getX(), (int) pos.getY(), 0, 0);
    }

    @Override
    public double getOffsetX() {
        return 0;
    }

    @Override
    public double getOffsetY() {
        return 0;
    }

}
