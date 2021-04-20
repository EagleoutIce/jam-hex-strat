package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.sound.SFXPlayback;
import de.gurkenlabs.litiengine.sound.Sound;
import de.gurkenlabs.litiengine.sound.SoundEngine;

public class SoundPoolPlayGroup {
    private SFXPlayback currently;
    private static final float DEFAULT_VOLUME = .5f;

    public void play(Sound sound) {
        play(sound, DEFAULT_VOLUME);
    }

    public void play(Sound sound, float volume) {
        if(currently != null)
            currently.cancel();
        currently = Game.audio().playSound(sound, false, SoundEngine.DEFAULT_MAX_DISTANCE, volume);
    }

}
