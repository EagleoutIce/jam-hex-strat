package de.flojo.jam.audio;

import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.sound.MusicPlayback;
import de.gurkenlabs.litiengine.sound.SoundEvent;
import de.gurkenlabs.litiengine.sound.SoundPlaybackListener;

import javax.naming.OperationNotSupportedException;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class BackgroundMusic {

    public static final AtomicBoolean toggleMusic = new AtomicBoolean();
    public static final Set<NamedTrack> TRACKS = Set.of(
            new NamedTrack("audio/background/backD.wav", "Track D"),
            new NamedTrack("audio/background/backC.wav", "Track C"),
            new NamedTrack("audio/background/backB.wav", "Track B"),
            new NamedTrack("audio/background/backA.wav", "Track A"));
    private static final BackgroundMusic instance = new BackgroundMusic();

    private BackgroundMusic() {
    }

    public static BackgroundMusic getInstance() {
        return instance;
    }

    public void enable() {
        Input.keyboard().onKeyTyped(KeyEvent.VK_P, keyEvent -> {
            if (toggleMusic.get()) {
                Game.audio().stopMusic();
                toggleMusic.set(false);
            } else {
                playNewBackgroundMusic();
            }
        });
    }

    public void disable() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    public void playNewBackgroundMusic() {
        new Thread(this::asyncStartBackgroundMusic).start();
    }

    private void asyncStartBackgroundMusic() {
        final NamedTrack track = Game.random().choose(TRACKS);
        HexStratLogger.log().log(Level.INFO, "Playing track: {0}", track.getName());
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
        Game.audio().getAllMusic().forEach(m -> m.setVolume(.08f));
        toggleMusic.set(true);
    }
}
