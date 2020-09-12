package de.flojo.jam.game.creature;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.flojo.jam.game.board.Tile;
import de.gurkenlabs.litiengine.graphics.IRenderable;

public class CreatureBase implements IRenderable {

    private Tile position;
    private Creature creature;
    private Rectangle2D hitBox;
    private boolean locked;



    @Override
    public void render(Graphics2D arg0) {
        // do this later ;)
    }




}
