package de.flojo.jam.game.terrain.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class VoidRenderer implements IRenderData {

    private VoidRenderer() {}

    private static VoidRenderer instance = new VoidRenderer();

    public static VoidRenderer get() {
        return instance;
    }


    @Override
    public void render(Graphics2D g, Point2D pos, boolean highlight) {
        // do nothing
    }

    @Override
    public boolean hasImage() {
        return false;
    }

    @Override
    public BufferedImage getImage() {
        return null;
    }
    
}
