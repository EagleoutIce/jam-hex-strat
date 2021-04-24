package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.util.ImageUtil;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class CreatureImagesHost implements IRenderData {
    public static final String FLY_LILA = "creatures/effects/fliegen_overlay_lila.png";
    public static final String FLY_BLAU = "creatures/effects/fliegen_overlay_blau.png";
    protected static final double GLOW_OFFSET_X = 1 / 11d;
    protected static final double GLOW_OFFSET_Y = 1 / 12d;
    protected final BufferedImage image;
    protected final BufferedImage highlightImage;
    protected final BufferedImage darkerImage;
    protected final BufferedImage darkerHighlightImage;
    protected final BufferedImage markImage;
    protected final BufferedImage glowImage;
    protected final BufferedImage flyImage;
    protected final float internalScale;
    protected Image scaledImage;

    protected CreatureImagesHost(
            final String path, final float internalScale, final String glowPath, final String flyPath) {
        this.internalScale = internalScale;
        this.image = Resources.images().get(path);
        highlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        darkerHighlightImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        markImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        glowImage = Resources.images().get(glowPath);
        flyImage = Resources.images().get(flyPath);
        new Thread(this::generateImageVariants).start();
    }

    protected void generateImageVariants() {
        ImageUtil.modifyRGBA(image, darkerImage, 0.6f, 0.6f, 0.6f, 1);
        ImageUtil.modifyRGBA(image, highlightImage, 1.36f, 1.36f, 1.36f, 1);
        ImageUtil.modifyRGBA(image, darkerHighlightImage, 0.8f, 0.8f, 0.8f, 1);
        ImageUtil.modifyRGBA(image, markImage, 1.2f, 1.2f, 1.2f, 1);
        scaledImage = ImageUtil.scale(image, internalScale);
    }

    protected float scale() {
        return Board.getZoom() * internalScale;
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
}
