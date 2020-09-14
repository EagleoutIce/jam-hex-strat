package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public interface IRenderData {

    default void render(Graphics2D g, Point2D pos, boolean doHighlight) {
        render(g, pos, doHighlight ? RenderHint.HIGHLIGHT : RenderHint.NORMAL);
    }
    void render(Graphics2D g, Point2D pos, RenderHint hint);

    boolean hasImage();

    BufferedImage getImage();

    public Rectangle2D getEffectiveRectangle(Point2D pos);
}
