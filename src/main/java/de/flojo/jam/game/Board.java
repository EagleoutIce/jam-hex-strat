package de.flojo.jam.game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.resources.Resources;

public class Board implements IRenderable {
    private final transient  BufferedImage background;

    private int width;
    private int height;
    private String backgroundPath;

    public Board(int w, int h, String backgroundPath) {
        this.width = w;
        this.height = h;
        this.backgroundPath = backgroundPath; 
        this.backgroundPath = Resources.images().get(backgroundPath);
    }

    @Override
    public void render(Graphics2D arg0) {
        // TODO Auto-generated method stub
    }

}
