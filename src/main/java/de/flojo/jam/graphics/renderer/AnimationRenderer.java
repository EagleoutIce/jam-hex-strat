package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.Spritesheet;

public class AnimationRenderer implements IRenderData {
    private int currentIndex;
    private final int totalFrameCount;
    private final int frameDuration;
    private long lastFrameUpdate = 0;
    private final Spritesheet spritesheet;
    private final double offsetX;
    private final double offsetY;
    private final int sWidth;
    private final int sHeight;

    private BufferedImage currentImg;

    public AnimationRenderer(Spritesheet spritesheet, int frameDuration, double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sWidth = spritesheet.getSpriteWidth();
        this.sHeight = spritesheet.getSpriteHeight();
        this.totalFrameCount = spritesheet.getColumns(); // ########### -- images, no rows!
        this.currentIndex = 0;
        this.frameDuration = frameDuration;
        this.spritesheet = spritesheet;
    }

    @Override
    public void render(final Graphics2D g, final Point2D pos, RenderHint ignored) {
        if(lastFrameUpdate == 0) {
            lastFrameUpdate = Game.time().now();
        }
        if(Game.time().since(lastFrameUpdate) >= this.frameDuration) {
            currentIndex = Math.min(currentIndex + 1, totalFrameCount -1);
            BufferedImage spriteImage = spritesheet.getSprite(this.currentIndex);
            currentImg = new BufferedImage(sWidth, sHeight, BufferedImage.TYPE_INT_ARGB);
            RescaleOp lighterOperation = new RescaleOp(new float[] { 1f, 1f, 1f, 1f * (1 - 0.5f*currentIndex/(float)totalFrameCount) }, new float[4], null);
            lighterOperation.filter(spriteImage, currentImg);
            lastFrameUpdate = Game.time().now();
        }
        ImageRenderer.render(g, currentImg, pos.getX() + offsetX, pos.getY() + offsetY);
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public BufferedImage getImage() {
        return currentImg;
    }

    @Override
    public Rectangle2D getEffectiveRectangle(Point2D pos) {
        return new Rectangle((int)(pos.getX() + offsetX), (int)(pos.getY() + offsetY), sWidth, sHeight);
    }
    
}