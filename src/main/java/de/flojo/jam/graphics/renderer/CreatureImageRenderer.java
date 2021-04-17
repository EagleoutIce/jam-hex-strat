package de.flojo.jam.graphics.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.resources.Resources;

public class CreatureImageRenderer implements IRenderData {
	private final BufferedImage image;
	private final double offsetX;
	private final double offsetY;
	private final BufferedImage highlightImage;
	private final BufferedImage darkerImage;
	private final BufferedImage darkerHighlightImage;
	private final BufferedImage markImage;
	private final BufferedImage glowImage;
	private final BufferedImage flyImage;
	private static final int GLOW_OFFSET_X = 10;
	private static final int GLOW_OFFSET_Y = 10;

	public static final String FLY_LILA = "creatures/effects/fliegen_overlay_lila.png";
	public static final String FLY_BLAU = "creatures/effects/fliegen_overlay_blau.png";

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

	private static RescaleOp modifyRGBA(float r, float g, float b, float a) {
		return new RescaleOp(new float[] { r, g, b, a }, new float[4], null);
	}

	public static boolean isTransparent(BufferedImage image, int x, int y ) {
		int pixel = image.getRGB(x,y);
		return (pixel>>24) == 0x00;
	}

	private void generateImageVariants() {
		modifyRGBA(0.6f, 0.6f, 0.6f, 1).filter(image, darkerImage);
		modifyRGBA(1.36f, 1.36f, 1.36f, 1).filter(image, highlightImage);
		modifyRGBA(0.8f, 0.8f, 0.8f, 1).filter(image, darkerHighlightImage);
		modifyRGBA(1.2f, 1.2f, 1.2f, 1).filter(image, markImage);
	}

	@Override
	public void render(final Graphics2D g, final Point2D pos, RenderHint... hints) {
		BufferedImage renderImage;
		for (RenderHint hint : hints) {
			switch(hint) {
				case GLOW:
					ImageRenderer.render(g,  glowImage, pos.getX() + offsetX - GLOW_OFFSET_X, pos.getY() + offsetY - GLOW_OFFSET_Y);
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
			ImageRenderer.render(g,  renderImage, pos.getX() + offsetX, pos.getY() + offsetY);
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
		return new Rectangle((int)(pos.getX() + offsetX), (int)(pos.getY() + offsetY), image.getWidth(), image.getHeight());
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