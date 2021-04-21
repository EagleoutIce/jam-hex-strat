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
    private static final int GLOW_OFFSET_X = 10;
    private static final int GLOW_OFFSET_Y = 10;
    private final BufferedImage image;
    private final double offsetX;
    private final double offsetY;
    private final BufferedImage highlightImage;
    private final BufferedImage darkerImage;
    private final BufferedImage darkerHighlightImage;
    private final BufferedImage markImage;
    private final BufferedImage glowImage;
    private final BufferedImage flyImage;

    public CreatureImageRenderer(final String path, final String glowPath, final String flyPath, final double offsetX, final double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = Resources.images().get(path);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint... hints) {
        BufferedImage renderImage;
        for (RenderHint hint : hints) {
            switch (hint) {
                case GLOW:
                    ImageRenderer.renderScaled(g, glowImage, pos.getX() + Board.zoom*(offsetX - GLOW_OFFSET_X), pos.getY() + Board.zoom*(offsetY - GLOW_OFFSET_Y), Board.zoom, Board.zoom);
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
            // TODO: zoom shift
            ImageRenderer.renderScaled(g, renderImage, pos.getX() + Board.zoom*(offsetX), pos.getY() + Board.zoom*(offsetY), Board.zoom, Board.zoom);
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
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle((int) (pos.getX() + Board.zoom*(offsetX)), (int) (pos.getY() + Board.zoom*(offsetY)), (int)(Board.zoom*image.getWidth()), (int)(Board.zoom*image.getHeight()));
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