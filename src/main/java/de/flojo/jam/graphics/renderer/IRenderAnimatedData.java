package de.flojo.jam.graphics.renderer;

public interface IRenderAnimatedData extends IRenderData {
    boolean completed();

    float getProgress();
}
