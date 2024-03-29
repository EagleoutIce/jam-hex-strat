package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.gurkenlabs.litiengine.graphics.IRenderable;

import java.awt.Graphics2D;

public class CreatureBase implements IRenderable {

    private static final int RAISED_TERRAIN_OFFSET = -32;
    private final Object targetLocationReachedLock = new Object();
    private Tile position;
    private int movementOffsetX = 0;
    private int movementOffsetY = 0;
    private double deltaX = 0;
    private double deltaY = 0;
    private float terrainOffsetY = 0;
    private boolean resetTerrainOffset = false;
    private CreatureCore core;

    public CreatureBase(Tile position) {
        this.position = position;
        updateTerrainOffset(position);
    }

    protected void assignCreature(CreatureCore core) {
        this.core = core;
        core.setBase(this);
    }

    public void moveOutFieldRaw(double x, double y) {
        movementOffsetX += x - position.getShiftedCenter().getX();
        movementOffsetY += y - position.getShiftedCenter().getY();
        deltaX = Math.abs(0.06 * movementOffsetX);
        deltaY = Math.abs(0.06 * movementOffsetY) + (movementOffsetY < 0 ? 1 : 0); // rundungs "ditsch" :D
        resetTerrainOffset = true;
        // we keep the old coordinate so the figure will get still drawn
        position = new Tile(position.getCoordinate(), position.getShiftedCenter().getX() + movementOffsetX,
                            position.getShiftedCenter().getY() + movementOffsetY, c -> TerrainTile.EMPTY);
    }

    public void move(Tile target) {
        movementOffsetX += target.getShiftedCenter().getX() - position.getShiftedCenter().getX();
        movementOffsetY += target.getShiftedCenter().getY() - position.getShiftedCenter().getY();
        deltaX = Math.abs(0.06 * movementOffsetX);
        deltaY = Math.abs(0.06 * movementOffsetY) + (movementOffsetY < 0 ? 1 : 0); // rundungs "ditsch" :D
        updateTerrainOffset(target);
        position = target;
    }

    private void updateTerrainOffset(Tile target) {
        if (target.getTerrainType().isRaised()) {
            terrainOffsetY = RAISED_TERRAIN_OFFSET;
        } else {
            resetTerrainOffset = true;
        }
    }

    public Object getTargetLocationReachedLock() {
        return targetLocationReachedLock;
    }

    public boolean moveTargetIsReached() {
        return movementOffsetX == 0 && movementOffsetY == 0;
    }

    @Override
    public void render(Graphics2D g) {
        if (movementOffsetX != 0)
            movementOffsetX = (int) (Math.signum(movementOffsetX) * Math.max(Math.abs(movementOffsetX) - deltaX, 0));
        if (movementOffsetY != 0)
            movementOffsetY = (int) (Math.signum(movementOffsetY) * Math.max(Math.abs(movementOffsetY) - deltaY, 0));

        if (moveTargetIsReached()) {
            synchronized (targetLocationReachedLock) {
                targetLocationReachedLock.notifyAll();
            }
        }

        core.render(g);
    }

    public Tile getTile() {
        return position;
    }

    public void setPosition(Tile position) {
        this.position = position;
        updateTerrainOffset(position);
    }

    public CreatureCore getCreature() {
        return core;
    }

    protected int getMovementOffsetX() {
        return movementOffsetX;
    }

    protected int getMovementOffsetY() {
        return movementOffsetY;
    }

    protected float getTerrainOffsetY() {
        if (resetTerrainOffset && Math.abs(movementOffsetX) <= 11 && Math.abs(movementOffsetY) <= 11) {
            terrainOffsetY = 0;
            resetTerrainOffset = false;

        }
        return terrainOffsetY;
    }

    @Override
    public String toString() {
        return "CreatureBase{" +
                "position=" + position +
                ", movementOffsetX=" + movementOffsetX +
                ", movementOffsetY=" + movementOffsetY +
                ", deltaX=" + deltaX +
                ", deltaY=" + deltaY +
                ", terrainOffsetY=" + terrainOffsetY +
                ", resetTerrainOffset=" + resetTerrainOffset +
                '}';
    }
}
