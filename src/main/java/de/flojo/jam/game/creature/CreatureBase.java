package de.flojo.jam.game.creature;

import java.awt.Graphics2D;

import de.flojo.jam.game.board.Tile;
import de.gurkenlabs.litiengine.graphics.IRenderable;

public class CreatureBase implements IRenderable {

    private Tile position;
    private int movementOffsetX = 0;
    private int movementOffsetY = 0;
    private CreatureCore core;
    private boolean locked = false;

    private final Object targetLocationReachedLock = new Object();

    public CreatureBase(Tile position) {
        this.position = position;
    }


    protected void assignCreature(CreatureCore core){
        this.core = core;
        core.setBase(this);
    }

    public void move(Tile target){
        movementOffsetX += target.getCenter().x - position.getCenter().x;
        movementOffsetY += target.getCenter().y - position.getCenter().y;
        position = target;
    }


    public Object getTargetLocationReachedLock() {
        return targetLocationReachedLock;
    }

    public boolean moveTargetReached() {
        return movementOffsetX == 0 && movementOffsetY == 0;
    }

    @Override
    public void render(Graphics2D g) {
        if(movementOffsetX != 0)
            movementOffsetX =  (int)(Math.signum(movementOffsetX) * Math.max(Math.abs(movementOffsetX) * 0.94, 0));
        if(movementOffsetY != 0)
            movementOffsetY = (int)(Math.signum(movementOffsetY) * Math.max(Math.abs(movementOffsetY) * 0.94, 0));
        
        if(moveTargetReached()) {
            synchronized(targetLocationReachedLock) {
                targetLocationReachedLock.notify();
            }
        }


        core.render(g);
    }

    public Tile getTile() {
        return position;
    }

    public void setPosition(Tile position) {
        this.position = position;
    }

    public CreatureCore getCreature() {
        return core;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected int getMovementOffsetX() {
        return movementOffsetX;
    }

    protected int getMovementOffsetY() {
        return movementOffsetY;
    }

}
