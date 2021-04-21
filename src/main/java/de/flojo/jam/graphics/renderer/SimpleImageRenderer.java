package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Board;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class SimpleImageRenderer implements IRenderData {
    protected final double offsetX;
    protected final double offsetY;
    private final BufferedImage image;
    private final BufferedImage highlightImage;
    private final BufferedImage markImage;

    public SimpleImageRenderer(final String path, final double offsetX, final double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = Resources.images().get(path);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        generateImageVariants();
    }

    private static RescaleOp modifyRGBA(float r, float g, float b, float a) {
        return new RescaleOp(new float[]{r, g, b, a}, new float[4], null);
    }

    public static boolean isTransparent(BufferedImage image, int x, int y) {
        int pixel = image.getRGB(x, y);
        return (pixel >> 24) == 0x00;
    }

    private void generateImageVariants() {
        modifyRGBA(1.32f, 1.32f, 1.32f, 1).filter(image, highlightImage);
        modifyRGBA(1.15f, 1.45f, 1.45f, 1).filter(image, markImage);
        modifyRGBA(1.32f, 1.32f, 1.32f, 1).filter(image, highlightImage);
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint... hints) {
        BufferedImage renderImage;
        if (hints == null || hints.length == 0)
            return;

        switch (hints[0]) {
            case HOVER:
                renderImage = highlightImage;
                break;
            case MARKED:
                renderImage = markImage;
                break;
            default:
            case NORMAL:
                renderImage = image;
                break;

        }
        ImageRenderer.renderScaled(g, renderImage, pos.getX() + Board.zoom*offsetX , pos.getY() + Board.zoom*offsetY, Board.zoom, Board.zoom);
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
        return new Rectangle((int) (pos.getX() + Board.zoom*offsetX), (int) (pos.getY() + Board.zoom*offsetY), (int)(Board.zoom*image.getWidth()), (int)(Board.zoom*image.getHeight()));
    }

    @Override
    public double getOffsetX() {
        return offsetX;
    }

    @Override
    public double getOffsetY() {
        return offsetY;
    }
}
