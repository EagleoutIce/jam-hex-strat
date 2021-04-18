package de.flojo.jam.game.creature;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.Main;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

public class CreatureCore {

	private IRenderData mainData;
	private IRenderData dyingData;

	IRenderData renderCore;

	private CreatureBase base;
	private CreatureAttributes attributes;

	private boolean isFlying = false;
	private boolean isDead = false;

	private boolean isOur;
	private final PlayerId owner;

	private AtomicBoolean highlight = new AtomicBoolean();

	public CreatureCore(PlayerId owner, boolean isOur, IRenderData mainData, IRenderData dyingData,
			CreatureAttributes attributes) {
		this.mainData = mainData;
		this.dyingData = dyingData;
		this.renderCore = this.mainData;
		this.attributes = attributes;
		this.owner = owner;
		this.isOur = isOur;
	}

	public void setBase(CreatureBase base) {
		this.base = base;
	}

	public CreatureAttributes getAttributes() {
		return attributes;
	}

	public PlayerId getOwner() {
		return owner;
	}

	public boolean isDead() {
		return isDead;
	}

	public void die() {
		isDead = true;
		unsetHighlight();
		renderCore = dyingData;
	}

	public void highlight() {
		this.highlight.set(true);
	}

	public void unsetHighlight() {
		this.highlight.set(false);
	}

	private RenderHint[] getRenderHints() {
		List<RenderHint> hints = new ArrayList<>();
		if (base.getTile().isMarked() || highlight.get()) {
			hints.add(RenderHint.GLOW);
			hints.add(RenderHint.MARKED);
		}

		if (base.getTile().isHovered()) {
			hints.add(attributes.canDoSomething() ? RenderHint.HOVER : RenderHint.DARK_HOVER);
		} else if (hints.isEmpty()) {
			hints.add(attributes.canDoSomething() ? RenderHint.NORMAL : RenderHint.DARK);
		}
		if (isFlying())
			hints.add(RenderHint.FLY);
		return hints.toArray(RenderHint[]::new);
	}

	protected void render(Graphics2D g) {
		final Point c = base.getTile().getCenter();
		final Point renderTarget = new Point(c.x - base.getMovementOffsetX(),
				c.y - base.getMovementOffsetY() + base.getTerrainOffsetY());
		renderCore.render(g, renderTarget, getRenderHints());
		if (isOur) {
			g.setFont(Main.TEXT_NORMAL);
			final String coords = Integer.toString(getAttributes().getApLeft()) + " / "
					+ Integer.toString(getAttributes().getMpLeft());
			renderTarget.translate((int) (-TextRenderer.getWidth(g, coords) / 2),
					(int) (-renderCore.getEffectiveRectangle(renderTarget).getHeight()) + 10);
			g.setColor(new Color(0f,0f,0f,.65f));
			Rectangle2D bound = TextRenderer.getBounds(g, coords);
			ShapeRenderer.render(g, new Rectangle((int)bound.getX()-3,(int)bound.getY()-3,(int)bound.getWidth()+6,(int)bound.getHeight()+6), renderTarget);
			g.setColor(Color.ORANGE);
			TextRenderer.render(g, coords, renderTarget);
		}
	}

	public boolean isFlying() {
		return isFlying;
	}

	public void toggleFly() {
		this.isFlying = !this.isFlying;
	}
}
