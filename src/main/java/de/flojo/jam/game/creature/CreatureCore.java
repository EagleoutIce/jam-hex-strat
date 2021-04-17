package de.flojo.jam.game.creature;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

public class CreatureCore {

	private IRenderData mainData;
	private IRenderData dyingData;

	IRenderData renderCore;

	private CreatureBase base;
	private CreatureAttributes attributes;

	private boolean isFlying = false;

	private boolean isDead = false;

	private final PlayerId owner;

	private AtomicBoolean highlight = new AtomicBoolean();

	public CreatureCore(PlayerId owner, IRenderData mainData, IRenderData dyingData, CreatureAttributes attributes) {
		this.mainData = mainData;
		this.dyingData = dyingData;
		this.renderCore = this.mainData;
		this.attributes = attributes;
		this.owner = owner;
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
		List<RenderHint>  hints = new ArrayList<>();
		if(base.getTile().isMarked() || highlight.get()) {
			hints.add(RenderHint.GLOW);
			hints.add(RenderHint.MARKED);
		}

		if (base.getTile().isHovered()) {
			hints.add(attributes.canDoSomething() ? RenderHint.HOVER : RenderHint.DARK_HOVER);
		} else if (hints.isEmpty()){
			hints.add(attributes.canDoSomething() ? RenderHint.NORMAL : RenderHint.DARK);
		}
		if(isFlying())
			hints.add(RenderHint.FLY);
		return hints.toArray(RenderHint[]::new);
	}

	protected void render(Graphics2D g) {
		final Point c = base.getTile().getCenter();
		final Point renderTarget = new Point(c.x - base.getMovementOffsetX(),
				c.y - base.getMovementOffsetY() + base.getTerrainOffsetY());
		renderCore.render(g, renderTarget, getRenderHints());
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 20f));
		final String coords = Integer.toString(getAttributes().getApLeft()) + " / " + Integer.toString(getAttributes().getMpLeft());
		renderTarget.translate((int)(-TextRenderer.getWidth(g, coords)/2), (int)(-renderCore.getEffectiveRectangle(renderTarget).getHeight()));
		TextRenderer.renderWithOutline(g, coords, renderTarget, Color.BLACK, true);
	}

	public boolean isFlying() {
		return isFlying;
	}

	public void toggleFly() {
		this.isFlying = !this.isFlying;
	}
}
