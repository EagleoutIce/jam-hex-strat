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
    BufferedImage markImage;


    public SimpleImageRenderer(final String path, final double offsetX, final double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = Resources.images().get(path);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        generateImageVariants();
    }

    private static RescaleOp modifyRGBA(float r, float g, float b, float a) {
        return new RescaleOp(new float[] { r, g, b, a }, new float[4], null);
    }

    private void generateImageVariants() {
        modifyRGBA(0.6f, 0.6f, 0.6f, 1).filter(image, darkerImage);
        modifyRGBA(1.32f, 1.32f, 1.32f, 1).filter(image, highlightImage);
        modifyRGBA(0.8f, 0.8f, 0.8f, 1).filter(image, darkerHighlightImage);
        modifyRGBA(1.15f, 1.45f, 1.45f, 1).filter(image, markImage);
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint hint) {
        final BufferedImage renderImage;
        switch(hint) {
            case HOVER:
                renderImage = highlightImage;
                break;
            case DARK:
                renderImage = darkerImage;
                break;
            case MARKED:
                renderImage = markImage;
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
