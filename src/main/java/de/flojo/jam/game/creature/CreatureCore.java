package de.flojo.jam.game.creature;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreatureCore {

    private final PlayerId owner;
    private final IRenderData mainData;
    private final IRenderData dyingData;
    private final CreatureAttributes attributes;
    private final boolean isOur;
    private final AtomicBoolean highlight = new AtomicBoolean();
    IRenderData renderCore;
    private CreatureBase base;
    private boolean isFlying = false;
    private boolean isDead = false;

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
        final Tile ourTile = base.getTile();
        final Point2D c = ourTile.getCenter();
        final Point2D renderTarget = new Point2D.Double(c.getX() - base.getMovementOffsetX() + ourTile.getShiftX(),
                                                        c.getY() - base.getMovementOffsetY() + base.getTerrainOffsetY() + ourTile.getShiftY());
        renderCore.render(g, renderTarget, getRenderHints());
        if (isOur && Creature.showMpAp.get()) {
            g.setFont(Main.TEXT_NORMAL);
            final String apInformation = getAttributes().getApLeft() + " / "
                    + getAttributes().getMpLeft();
            renderTarget.setLocation(renderTarget.getX() + (-TextRenderer.getWidth(g, apInformation) / 2),
                                     renderTarget.getY() + (-renderCore.getEffectiveRectangle(
                                             renderTarget).getHeight()) + 10);
            final Color sc;
            if (base.getTile().isMarked() || highlight.get()) {
                g.setColor(new Color(.96f, .97f, .96f, .85f));
                sc = Color.BLACK;
            } else {
                g.setColor(new Color(0f, 0f, 0f, .65f));
                sc = Color.ORANGE;
            }
            Rectangle2D bound = TextRenderer.getBounds(g, apInformation);
            ShapeRenderer.render(g, new Rectangle((int) bound.getX() - 3, (int) bound.getY() - 3,
                                                  (int) bound.getWidth() + 6, (int) bound.getHeight() + 6),
                                 renderTarget);
            g.setColor(sc);
            TextRenderer.render(g, apInformation, renderTarget);
        }
    }

    public boolean isFlying() {
        return isFlying;
    }

    public boolean toggleFly() {
        this.isFlying = !this.isFlying;
        return this.isFlying;
    }

    @Override
    public String toString() {
        return "CreatureCore{" +
                "owner=" + owner +
                ", attributes=" + attributes +
                ", isOur=" + isOur +
                ", highlight=" + highlight +
                ", isFlying=" + isFlying +
                ", isDead=" + isDead +
                '}';
    }
}
