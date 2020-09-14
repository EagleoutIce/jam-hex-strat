package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.resources.Resources;

public class SimpleImageRenderer implements IRenderData {
    private final BufferedImage image;
    private final double offsetX;
    private final double offsetY;
    BufferedImage highlightImage;
    BufferedImage darkerImage;
    BufferedImage darkerHighlightImage;


    public SimpleImageRenderer(final String path, final double offsetX, final double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = Resources.images().get(path);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        RescaleOp darkerOperation = new RescaleOp(new float[] { 0.6f, 0.6f, 0.6f, 1 }, new float[4], null);
        darkerOperation.filter(image, darkerImage);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        RescaleOp lighterOperation = new RescaleOp(new float[] { 1.32f, 1.32f, 1.32f, 1 }, new float[4], null);
        lighterOperation.filter(image, highlightImage);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        RescaleOp darkerHighlightOperation = new RescaleOp(new float[] { 0.8f, 0.8f, 0.8f, 1 }, new float[4], null);
        darkerHighlightOperation.filter(image, darkerHighlightImage);
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint hint) {
        final BufferedImage renderImage;
        switch(hint) {
            case HIGHLIGHT:
                renderImage = highlightImage;
                break;
            case DARK:
                renderImage = darkerImage;
                break;
            default:
            case NORMAL:
                renderImage = image;
                break;

        }
        ImageRenderer.render(g,  renderImage, pos.getX() + offsetX, pos.getY() + offsetY);
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle((int)(pos.getX() + offsetX), (int)(pos.getY() + offsetY), image.getWidth(), image.getHeight());
    }
    
}
