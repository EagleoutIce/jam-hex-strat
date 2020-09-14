package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class VoidRenderer implements IRenderData {

    private VoidRenderer() {
    }

    private static VoidRenderer instance = new VoidRenderer();

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
        return new Rectangle((int)pos.getX(), (int)pos.getY(), 0, 0);
    }
    
}
