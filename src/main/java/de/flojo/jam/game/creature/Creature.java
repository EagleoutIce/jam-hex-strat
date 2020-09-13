package de.flojo.jam.game.creature;

import java.awt.Graphics2D;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.skills.DefaultEffectContext;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;

// compound of base and core; mostly delegates
public class Creature implements IRenderable {

    private final String name;
    private final CreatureBase base;
    private final CreatureCore core;
    private final CreatureCollection cCollection;
    private final TrapCollection tCollection;

    protected static final int DIE_DURATION = 800;

    public Creature(final String name,final CreatureCollection collection, final TrapCollection traps, CreatureBase base, CreatureCore core) {
        this.base = base;
        this.core = core;
        this.name = name;
        this.cCollection = collection;
        this.cCollection.add(this);
        this.tCollection = traps;
        this.base.assignCreature(core);
    }

    public PlayerId getOwner() {
        return core.getOwner();
    }

    public CreatureBase getBase() {
        return base;
    }

    public CreatureCore getCore() {
        return core;
    }

    public int getX() {
        return this.getCoordinate().x;
    }

    public int getY() {
        return this.getCoordinate().y;
    }

    public BoardCoordinate getCoordinate() {
        return this.getBase().getTile().getCoordinate();
    }

    public String getName() {
        return name;
    }

    public void move(Tile target) {
        if(!dead()) {
            return;
        }
        base.move(target);
    }

    public boolean dead() {
        return core.isDead();
    }

    @Override
    public void render(Graphics2D g) {
        this.base.render(g);
    }
    
    public void useSkill(Board board, SkillId skill, Creature target) {
        this.core.getAttributes().useSkill(new DefaultEffectContext(board, cCollection, tCollection), skill, this, target);
    }

    public void useSkill(IProvideEffectContext context, SkillId skill, Creature target) {
        this.core.getAttributes().useSkill(context, skill, this, target);
    }
    
    public Object moveLock() {
        return base.getTargetLocationReachedLock();
    }

    public void clearHover() {
        this.base.getTile().clearHover();
    }

    public void setHover() {
        this.base.getTile().setHover();
    }

    public boolean isHovered() {
        return this.base.getTile().isHovered();
    }

    public CreatureAttributes getAttributes() {
        return this.core.getAttributes();
    }

    public void die() {
        core.die();
        Game.loop().perform(DIE_DURATION, () -> cCollection.remove(this));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Creature [").append("name=").append(name)
                .append(", position=").append(getCoordinate()).append(", owner=").append(this.core.getOwner()).append("]");
        return builder.toString();
    }
}
