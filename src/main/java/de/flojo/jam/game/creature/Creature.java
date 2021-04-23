package de.flojo.jam.game.creature;

import de.flojo.jam.audio.BackgroundMusic;
import de.flojo.jam.audio.SoundPoolPlayGroup;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.DefaultReadContext;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;

import java.awt.Graphics2D;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

// compound of base and core; mostly delegates
public class Creature implements IRenderable {

    public static final SoundPoolPlayGroup soundPlayGroup = new SoundPoolPlayGroup(BackgroundMusic.toggleMusic::get);
    public static final AtomicBoolean showMpAp = new AtomicBoolean(true);
    protected static final int DIE_DURATION = 1200;
    private final String name;
    private final CreatureId creatureId;
    private final CreatureBase base;
    private final CreatureCore core;
    private final CreatureCollection cCollection;
    private final TrapCollection tCollection;
    // is dying
    private boolean moribund = false;
    private Runnable onDead;

    public Creature(final CreatureId creatureId, final String name, final CreatureCollection collection,
                    final TrapCollection traps, CreatureBase base, CreatureCore core) {
        this.creatureId = creatureId;
        this.base = base;
        this.core = core;
        this.name = name;
        this.cCollection = collection;
        if (cCollection != null)
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

    public void moveBlocking(Tile target) {
        move(target);
        try {
            CreatureActionController.awaitMovementComplete(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void move(Tile target) {
        if (dead())
            return;
        base.move(target);
    }

    // only one so no wrong clear
    public void setOnDead(Runnable onDead) {
        this.onDead = onDead;
    }

    public boolean dead() {
        return core.isDead();
    }

    @Override
    public void render(Graphics2D g) {
        this.base.render(g);
    }

    public void useSkill(Board board, JsonDataOfSkill wantedSkill, Creature target) {
        useSkill(new DefaultReadContext(board, cCollection, tCollection), wantedSkill, target);
    }

    public void useSkill(IProvideReadContext context, JsonDataOfSkill wantedSkill, Creature target) {
        this.core.getAttributes().useSkill(context, wantedSkill, this, target);
    }

    public void useSkill(IProvideReadContext context, AbstractSkill skill, Tile target) {
        this.core.getAttributes().useSkill(context, skill, this, target);
    }

    public void useSkill(IProvideReadContext context, AbstractSkill skill, Creature target) {
        this.core.getAttributes().useSkill(context, skill, this, target);
    }

    public void useSkill(Board board, AbstractSkill skill, Tile target) {
        useSkill(new DefaultReadContext(board, cCollection, tCollection), skill, target);
    }

    public void useSkill(Board board, AbstractSkill skill, Creature target) {
        useSkill(new DefaultReadContext(board, cCollection, tCollection), skill, target);
    }

    public Object moveLock() {
        return base.getTargetLocationReachedLock();
    }

    public void unsetHighlight() {
        core.unsetHighlight();
    }

    public void highlight() {
        core.highlight();
    }

    public boolean isHovered() {
        return this.base.getTile().isHovered();
    }

    public CreatureAttributes getAttributes() {
        return this.core.getAttributes();
    }

    public Optional<AbstractSkill> getSkill(JsonDataOfSkill wantedSkill) {
        return this.core.getAttributes().getSkill(wantedSkill);
    }

    public void die() {
        core.die();
        if (onDead != null)
            onDead.run();
        Game.loop().perform(DIE_DURATION, () -> cCollection.remove(this));
    }

    public boolean isFlying() {
        return core.isFlying();
    }

    public boolean isNotRaised() {
        return !base.getTile().getTerrainType().isRaised();
    }

    public boolean canDoSomething() {
        return !isMoribund() && !core.isDead() && getAttributes().canDoSomething();
    }

    public void skip() {
        getAttributes().setUsed();
    }

    public CreatureId getCreatureId() {
        return creatureId;
    }

    public void moribund() {
        moribund = true;
    }

    public boolean isMoribund() {
        return this.moribund;
    }

    public boolean canCastSkill(ICreatureSkill skill) {
        final boolean canCastFly = this.isFlying() && skill.castOnAir() || !this.isFlying() && skill.castOnGround();
        return this.getAttributes().getApLeft() >= skill.getCost() && canCastFly;
    }

    @Override
    public String toString() {
        return "Creature{" +
                "name='" + name + '\'' +
                ", creatureId=" + creatureId +
                ", base=" + base +
                ", core=" + core +
                ", moribund=" + moribund +
                '}';
    }
}
