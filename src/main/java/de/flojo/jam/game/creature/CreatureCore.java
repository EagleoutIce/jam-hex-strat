package de.flojo.jam.game.creature;

import java.awt.Graphics2D;
import java.awt.Point;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.gurkenlabs.litiengine.graphics.IRenderable;

public class CreatureCore implements IRenderable {

    private IRenderData mainData;
    private IRenderData dyingData;
    
    IRenderData renderCore;

    private CreatureBase base;
    private CreatureAttributes attributes;

    private boolean isFlying = false;

    private boolean isDead = false;
    private boolean isDying = false;
    private boolean dieAnimationCompleted = false;

    private final PlayerId owner;

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
        isDead = isDying = true;
        renderCore = dyingData;
    }

    @Override
    public void render(Graphics2D g) {
        final Point c = base.getTile().getCenter();
        renderCore.render(g, new Point(c.x - base.getMovementOffsetX(), c.y - base.getMovementOffsetY()),
                base.getTile().isHovered());
    }
}
