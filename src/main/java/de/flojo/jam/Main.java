package de.flojo.jam;

import com.google.gson.Gson;
import de.flojo.jam.audio.BackgroundMusic;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.EditorScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.screens.ServerSetupScreen;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.HexStratConfiguration;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameListener;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.screens.Resolution;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {
    public static final Image DEFAULT_CURSOR = Resources.images().get("cursor.png").getScaledInstance(30, 30,
                                                                                                      Image.SCALE_SMOOTH);
    public static final Image ICON = Resources.images().get("icon.png").getScaledInstance(42, 42, Image.SCALE_SMOOTH);

    public static final Font GUI_FONT = Resources.fonts().get("FFF_Tusj.ttf", 64f);
    public static final Font GUI_FONT_LARGE = GUI_FONT.deriveFont(75f);
    public static final Font GUI_FONT_SMALL = GUI_FONT.deriveFont(48f);
    public static final Font TEXT_STATUS = GUI_FONT.deriveFont(25f);
    public static final Font TEXT_NORMAL = GUI_FONT.deriveFont(12f);
    public static final float DEFAULT_INTERNAL_SCALE = .26f;
    public static final double INNER_MARGIN = 20d;

    private static HexStratConfiguration hexStratConfiguration;

    private static Thread loadEditor;
    private static Thread loadServer;
    private static int version = -1;

    public static HexStratConfiguration maybeLoadLocalConfigurationFile(String filename) {
        final var tempFile = new File(filename);
        if (!tempFile.exists()) {
            HexStratLogger.log().log(Level.INFO, "Configuration file \"{0}\" not found", filename);
            return new HexStratConfiguration();
        }
        final var gson = new Gson();
        try (final var reader = new FileReader(tempFile)) {
            final var config = gson.fromJson(reader, HexStratConfiguration.class);
            HexStratLogger.log().log(Level.INFO, "Loading from configuration file \"{0}\"", filename);
            return config;
        } catch (IOException e) {
            HexStratLogger.log().log(Level.WARNING, e.getMessage());
        }
        return new HexStratConfiguration();
    }

    public static void main(String[] args) {

        hexStratConfiguration = maybeLoadLocalConfigurationFile("hex-strat.conf");

        try (final var scanner = new Scanner(Resources.get("version.info"))) {
            version = scanner.nextInt();
        }

        Game.setInfo("info.xml");
        Game.addGameListener(new GameListener() {
            @Override
            public void initialized(String... args) {
                HexStratLogger.log().log(Level.INFO, "Initialized with: {0}", Arrays.toString(args));
            }

            @Override
            public void started() {
                Game.window().setResolution(Resolution.Ratio16x9.RES_1600x900);
                Game.window().getRenderComponent().setPreferredSize(new Dimension(1600, 900));
                HexStratLogger.log().info("Started Game Core");
            }

            @Override
            public void terminated() {
                HexStratLogger.log().info("Terminated");
            }
        });

        Game.init(args);
        Arrays.stream(HexStratLogger.log().getHandlers()).forEach(h -> h.setLevel(Level.ALL));
        Game.graphics().setBaseRenderScale(2f);
        Game.window().setTitle("Hex-Strat");

        Game.setUncaughtExceptionHandler((Thread t, Throwable ex) -> onUnhandledException(ex));

        GuiProperties.getDefaultAppearance().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceDisabled().setTextAntialiasing(true);
        GuiProperties.getDefaultAppearanceHovered().setTextAntialiasing(true);

        Game.window().cursor().set(DEFAULT_CURSOR);
        Game.window().setIcon(ICON);

        Game.screens().add(new MenuScreen());
        Game.screens().add(GameScreen.get());
        Game.screens().add(ConnectScreen.get());
        loadEditor = new Thread(() -> Game.screens().add(new EditorScreen()));
        loadServer = new Thread(() -> Game.screens().add(new ServerSetupScreen()));
        Game.start();

        loadServer.start();
        loadEditor.start();

        BackgroundMusic.getInstance().enable();
    }


    @SuppressWarnings("java:S106")
    private static void onUnhandledException(final Throwable ex) {
        HexStratLogger.log().severe(ex.getMessage());
        ex.printStackTrace();
    }

    public static Thread getStateOfEditorLoad() {
        return loadEditor;
    }

    public static Thread getStateOfServerLoad() {
        return loadServer;
    }

    public static int getVersion() {
        return version;
    }

    public static HexStratConfiguration getHexStratConfiguration() {
        return hexStratConfiguration;
    }
}
