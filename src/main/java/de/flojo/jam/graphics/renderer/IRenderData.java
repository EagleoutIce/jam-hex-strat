package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public interface IRenderData {
    default void render(Graphics2D g, Point2D pos, boolean doHighlight) {
        render(g, pos, doHighlight ? RenderHint.HOVER : RenderHint.NORMAL);
    }

    void render(Graphics2D g, Point2D pos, RenderHint... hints);

    boolean hasImage();

    BufferedImage getImage();

    Image getImageScaled();

    double getOffsetX();

    double getOffsetY();

    Rectangle2D getEffectiveRectangle(Point2D pos);
}
