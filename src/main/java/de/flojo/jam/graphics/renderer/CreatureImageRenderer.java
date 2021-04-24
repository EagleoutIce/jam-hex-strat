package de.flojo.jam.graphics.renderer;

import de.gurkenlabs.litiengine.graphics.ImageRenderer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CreatureImageRenderer extends CreatureImagesHost {
    private final double offsetX;
    private final double offsetY;

    public CreatureImageRenderer(final String path, final String glowPath, final String flyPath, final double offsetX,
                                 final double offsetY) {
        this(path, glowPath, flyPath, offsetX, offsetY, 1f);
    }

    public CreatureImageRenderer(final String path, final String glowPath, final String flyPath, final double offsetX,
                                 final double offsetY, final float internalScale) {
        super(path, internalScale, glowPath, flyPath);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint... hints) {
        BufferedImage renderImage;
        for (RenderHint hint : hints) {
            switch (hint) {
                case GLOW:
                    ImageRenderer.renderScaled(g, glowImage,
                                               pos.getX() + scale() * (offsetX - glowImage.getWidth() * GLOW_OFFSET_X),
                                               pos.getY() + scale() * (offsetY - glowImage.getHeight() * GLOW_OFFSET_Y),
                                               scale(), scale());
                    continue;
                case HOVER:
                    renderImage = highlightImage;
                    break;
                case DARK:
                    renderImage = darkerImage;
                    break;
                case MARKED:
                    renderImage = markImage;
                    break;
                case FLY:
                    renderImage = flyImage;
                    break;
                case NORMAL:
                    renderImage = image;
                    break;
                default:
                    renderImage = null;
                    break;
            }
            if (renderImage != null)
                ImageRenderer.renderScaled(g, renderImage, pos.getX() + scale() * offsetX,
                                           pos.getY() + scale() * offsetY,
                                           scale(), scale());
        }
    }

    @Override
    public double getOffsetX() {
        return offsetX;
    }

    @Override
    public double getOffsetY() {
        return offsetY;
    }


    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle2D.Double(pos.getX() + scale() * (offsetX), pos.getY() + scale() * (offsetY),
                                      scale() * image.getWidth(), scale() * image.getHeight());
    }
}
