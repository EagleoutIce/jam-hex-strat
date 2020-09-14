package de.flojo.jam.graphics.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RescaleOp;

import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.resources.Resources;

public class SimpleImageRenderer implements IRenderData {
    private final BufferedImage image;
    private final double offsetX;
    private final double offsetY;
    private final BufferedImage highlightImage;
    private final BufferedImage darkerImage;
    private final BufferedImage darkerHighlightImage;
    private final BufferedImage markImage;
    private final BufferedImage glowImage;
    private static final float GLOW_SCALE = 1.1f;
    private final int glowOffsetX;
    private final int glowOffsetY;
    private static final int GLOW_WHITE = new Color(187, 187, 187).getRGB();

    public SimpleImageRenderer(final String path, final double offsetX, final double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = Resources.images().get(path);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        glowImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        glowOffsetX = (int)(((GLOW_SCALE * image.getWidth()) - image.getWidth())/2d);
        glowOffsetY = (int)(((GLOW_SCALE * image.getHeight()) - image.getHeight())/2d);
        generateImageVariants();
    }

    private static RescaleOp modifyRGBA(float r, float g, float b, float a) {
        return new RescaleOp(new float[] { r, g, b, a }, new float[4], null);
    }

    public static boolean isTransparent(BufferedImage image, int x, int y ) {
        int pixel = image.getRGB(x,y);
        return (pixel>>24) == 0x00;
    }

    private void generateImageVariants() {
        modifyRGBA(0.6f, 0.6f, 0.6f, 1).filter(image, darkerImage);
        modifyRGBA(1.32f, 1.32f, 1.32f, 1).filter(image, highlightImage);
        modifyRGBA(0.8f, 0.8f, 0.8f, 1).filter(image, darkerHighlightImage);
        modifyRGBA(1.15f, 1.45f, 1.45f, 1).filter(image, markImage);
        generateGlowVariant();
    }

    private void generateGlowVariant() {
        int[] glowRaster = ((DataBufferInt) glowImage.getRaster().getDataBuffer()).getData();
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if(isTransparent(image, x, y))
                    glowRaster[x + y * w] = 0;
                else 
                    glowRaster[x + y * w] = GLOW_WHITE;
            }
        }
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint hint) {
        final BufferedImage renderImage;
        switch(hint) {
            case GLOW:
                ImageRenderer.renderScaled(g, glowImage, pos.getX() + offsetX - glowOffsetX, pos.getY() + offsetY - glowOffsetY, GLOW_SCALE);
                renderImage = highlightImage;
                break;
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
