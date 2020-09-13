package de.flojo.jam;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import de.flojo.jam.screens.EditorScreen;
import de.flojo.jam.screens.IngameScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.screens.ServerSetupScreen;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameListener;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.screens.Resolution;
import de.gurkenlabs.litiengine.resources.Resources;

/**
 * Main
 */
public class Main {

    public static final BufferedImage DEFAULT_CURSOR = Resources.images().get("cursor.png");
    public static final BufferedImage ICON = Resources.images().get("icon.png");

    public static final Font GUI_FONT = Resources.fonts().get("FFF_Tusj.ttf", 64f);
    public static final Font GUI_FONT_SMALL = GUI_FONT.deriveFont(48f);
    public static final Font TEXT_STATUS = GUI_FONT.deriveFont(25f);
    public static final Font TEXT_NORMAL = GUI_FONT.deriveFont(12f);

    public static final double INNER_MARGIN = 20d;
    public static final int BOARD_WIDTH = 24;
    public static final int BOARD_HEIGHT = 33;

    public static final String FIELD_BACKGROUND = "field-background.png";
    // TODO: overwrite mode to allow replacement? probably not.. :D


    public static void main(String[] args) {

        Game.setInfo("info.xml");
        Game.init(args);
        Game.log().setLevel(Level.ALL);
        Game.graphics().setBaseRenderScale(3.0f);

        Game.window().setTitle("Super duper game");

        Game.setUncaughtExceptionHandler((Thread t, Throwable ex) -> {
            System.err.println("Mimimi do this with a stream");
            ex.printStackTrace();
        });

        GuiProperties.getDefaultAppearance().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceDisabled().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceHovered().setTextAntialiasing(true);

        Game.window().cursor().set(DEFAULT_CURSOR);
        Game.window().setIcon(ICON);

        Game.screens().add(new MenuScreen());
        Game.screens().add(new IngameScreen());
        Game.screens().add(new EditorScreen());
        Game.screens().add(new ServerSetupScreen());
        
        Game.addGameListener(new GameListener() {
            @Override
            public void initialized(String... args) {
                // do sth when game is initialized
            }

            @Override
            public void started() {
                Game.window().setResolution(Resolution.Ratio16x9.RES_1600x900);

                // ((JFrame) Game.window().getHostControl()).setResizable(false);
                Game.window().getRenderComponent().setPreferredSize(new Dimension(1600, 900));
            }

            @Override
            public void terminated() {
                // do sth when game terminated
            }
        });

        Game.start();
    }
}