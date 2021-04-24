package de.flojo.jam.game;

import de.flojo.jam.Main;

import java.awt.Color;
import java.awt.Font;

public final class TileConstants {
    public static final int DEFAULT_RADIUS = 30;
    public static final Font NUMBER_FONT = Main.TEXT_NORMAL.deriveFont(20f);
    public static final Color HIGHLIGHT_COLOR = new Color(0.6f, 0.6f, 0.3f, 0.25f);
    public static final Color MARK_COLOR = new Color(0.3f, 0.45f, 0.3f, 0.475f);
    public static final Color DEFAULT_COLOR = new Color(0.4f, 0.6f, 0.3f, 0.15f);
    public static final Color NONE_COLOR = new Color(0, 0, 0, 35); // 154, 215, 45
    public static final Color P1_COLOR = new Color(45, 173, 215, 35);
    public static final Color P2_COLOR = new Color(141, 45, 215, 35);
    private TileConstants() {
    }
}
