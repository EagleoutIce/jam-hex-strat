package de.flojo.jam;

import de.flojo.jam.audio.NamedTrack;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.EditorScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.screens.ServerSetupScreen;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.HexStartLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameListener;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.screens.Resolution;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.LoopedTrack;
import de.gurkenlabs.litiengine.sound.MusicPlayback;
import de.gurkenlabs.litiengine.sound.SoundEvent;
import de.gurkenlabs.litiengine.sound.SoundPlaybackListener;
import de.gurkenlabs.litiengine.sound.Track;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Main {

    public static final BufferedImage DEFAULT_CURSOR = Resources.images().get("cursor.png");
    public static final BufferedImage ICON = Resources.images().get("icon.png");

    public static final Font GUI_FONT = Resources.fonts().get("FFF_Tusj.ttf", 64f);
    public static final Font GUI_FONT_SMALL = GUI_FONT.deriveFont(48f);
    public static final Font TEXT_STATUS = GUI_FONT.deriveFont(25f);
    public static final Font TEXT_NORMAL = GUI_FONT.deriveFont(12f);

    public static final double INNER_MARGIN = 20d;
    public static final AtomicBoolean toggleMusic = new AtomicBoolean();
    public static  final Set<NamedTrack> TRACKS = Set.of(
            new NamedTrack("audio/background/backD.wav", "Track D"),
            new NamedTrack("audio/background/backC.wav", "Track C"),
            new NamedTrack("audio/background/backB.wav", "Track B"),
            new NamedTrack("audio/background/backA.wav", "Track A"));
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
            HexStartLogger.log().severe(ex.getMessage());
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

        Input.keyboard().onKeyTyped(KeyEvent.VK_P, keyEvent -> {
            if(toggleMusic.get()) {
                Game.audio().stopMusic();
                toggleMusic.set(false);
            } else {
                playNewBackgroundMusic();
            }
        });
    }

    private static void playNewBackgroundMusic() {
        new Thread(Main::asyncStartBackgroundMusic).start();
    }

    private static void asyncStartBackgroundMusic() {
        final NamedTrack track = Game.random().choose(TRACKS);
        HexStartLogger.log().log(Level.INFO,"Playing track: {0}", track.getName());
        final MusicPlayback playing = Game.audio().playMusic(track);
        playing.addSoundPlaybackListener(new SoundPlaybackListener() {
            @Override
            public void cancelled(SoundEvent event) {
                SoundPlaybackListener.super.cancelled(event);
            }

            @Override
            public void finished(SoundEvent event) {
                SoundPlaybackListener.super.finished(event);
                playNewBackgroundMusic();
            }
        });
        Game.audio().getAllMusic().forEach(m -> m.setVolume(.1f));
        toggleMusic.set(true);
    }
}