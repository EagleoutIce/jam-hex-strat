package de.flojo.jam;

import de.flojo.jam.audio.BackgroundMusic;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.EditorScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.screens.ServerSetupScreen;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameListener;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.screens.Resolution;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.logging.Level;

public class Main {

    public static final Image DEFAULT_CURSOR = Resources.images().get("cursor.png").getScaledInstance(30,30, Image.SCALE_SMOOTH);
    public static final BufferedImage ICON = Resources.images().get("icon.png");

    public static final Font GUI_FONT = Resources.fonts().get("FFF_Tusj.ttf", 64f);
    public static final Font GUI_FONT_LARGE = GUI_FONT.deriveFont(75f);
    public static final Font GUI_FONT_SMALL = GUI_FONT.deriveFont(48f);
    public static final Font TEXT_STATUS = GUI_FONT.deriveFont(25f);
    public static final Font TEXT_NORMAL = GUI_FONT.deriveFont(12f);

    public static final double INNER_MARGIN = 20d;

    public static void main(String[] args) {

        Game.setInfo("info.xml");
        Game.addGameListener(new GameListener() {
            @Override
            public void initialized(String... args) {
                // nothing
            }

            @Override
            public void started() {
                Game.window().setResolution(Resolution.Ratio16x9.RES_1600x900);
                Game.window().getRenderComponent().setPreferredSize(new Dimension(1600, 900));
            }

            @Override
            public void terminated() {
                // nothing
            }
        });

        Game.init(args);
        Arrays.stream(Game.log().getHandlers()).forEach(h -> h.setLevel(Level.ALL));
        Game.graphics().setBaseRenderScale(3.0f);

        Game.window().setTitle("Super duper game");

        Game.setUncaughtExceptionHandler((Thread t, Throwable ex) -> {
            System.err.println("Mimimi do this with a stream");
            HexStratLogger.log().severe(ex.getMessage());
            ex.printStackTrace();
        });

        GuiProperties.getDefaultAppearance().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceDisabled().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceHovered().setTextAntialiasing(true);

        Game.window().cursor().set(DEFAULT_CURSOR);
        Game.window().setIcon(ICON);

        Game.screens().add(new MenuScreen());
        Game.screens().add(GameScreen.get());
        Game.screens().add(new EditorScreen());
        Game.screens().add(ConnectScreen.get());
        Game.screens().add(new ServerSetupScreen());

        Game.start();

        BackgroundMusic.getInstance().enable();
    }

}