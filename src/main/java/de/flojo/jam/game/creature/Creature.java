package de.flojo.jam.game.creature;

import java.awt.Graphics2D;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.gurkenlabs.litiengine.graphics.IRenderable;

// compound of base and core; mostly delegates
public class Creature implements IRenderable {

    private final String name;
    private final CreatureBase base;
    private final CreatureCore core;

    public Creature(final String name, CreatureBase base, CreatureCore core) {
        this.base = base;
        this.core = core;
        this.name = name;
        this.base.assignCreature(core);
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
        base.move(target);
    }

    @Override
    public void render(Graphics2D g) {
        this.core.render(g);
    }
    
    

}
