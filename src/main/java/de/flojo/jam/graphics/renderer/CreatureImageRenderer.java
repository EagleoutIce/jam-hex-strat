package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.util.ImageUtil;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CreatureImageRenderer implements IRenderData {
    public static final String FLY_LILA = "creatures/effects/fliegen_overlay_lila.png";
    public static final String FLY_BLAU = "creatures/effects/fliegen_overlay_blau.png";
    private static final double GLOW_OFFSET_X = 1/11d;
    private static final double GLOW_OFFSET_Y = 1/12d;
    private final BufferedImage image;
    private final Image scaledImage;
    private final double offsetX;
    private final double offsetY;
    private final BufferedImage highlightImage;
    private final BufferedImage darkerImage;
    private final BufferedImage darkerHighlightImage;
    private final BufferedImage markImage;
    private final BufferedImage glowImage;
    private final BufferedImage flyImage;
    private final float internalScale;

    public CreatureImageRenderer(final String path, final String glowPath, final String flyPath, final double offsetX, final double offsetY) {
        this(path, glowPath, flyPath, offsetX, offsetY, 1f);
    }

    public CreatureImageRenderer(final String path, final String glowPath, final String flyPath, final double offsetX, final double offsetY, final float internalScale) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.internalScale = internalScale;
        this.image = Resources.images().get(path);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        scaledImage = ImageUtil.scale(image, internalScale);
        glowImage = Resources.images().get(glowPath);
        flyImage = Resources.images().get(flyPath);
        generateImageVariants();
    }

    private void generateImageVariants() {
        ImageUtil.modifyRGBA(image, darkerImage, 0.6f, 0.6f, 0.6f, 1);
        ImageUtil.modifyRGBA(image, highlightImage, 1.36f, 1.36f, 1.36f, 1);
        ImageUtil.modifyRGBA(image, darkerHighlightImage, 0.8f, 0.8f, 0.8f, 1);
        ImageUtil.modifyRGBA(image, markImage, 1.2f, 1.2f, 1.2f, 1);
    }

    private float scale() {
        return Board.getZoom() * internalScale;
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint... hints) {
        BufferedImage renderImage;
        for (RenderHint hint : hints) {
            switch (hint) {
                case GLOW:
                    ImageRenderer.renderScaled(g, glowImage, pos.getX() + scale() * (offsetX - glowImage.getWidth() * GLOW_OFFSET_X), pos.getY() + scale() * (offsetY - glowImage.getHeight() * GLOW_OFFSET_Y), scale(), scale());
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
                default:
                case NORMAL:
                    renderImage = image;
                    break;
            }
            ImageRenderer.renderScaled(g, renderImage, pos.getX() + scale() * offsetX, pos.getY() + scale() * offsetY, scale(), scale());
        }
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
    public Image getImageScaled() {
        return scaledImage;
    }

    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle((int) (pos.getX() + scale() * (offsetX)), (int) (pos.getY() + scale() * (offsetY)), (int) (scale() * image.getWidth()), (int) (scale() * image.getHeight()));
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