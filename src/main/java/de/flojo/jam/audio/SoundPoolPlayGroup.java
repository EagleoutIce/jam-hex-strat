package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.sound.SFXPlayback;
import de.gurkenlabs.litiengine.sound.Sound;
import de.gurkenlabs.litiengine.sound.SoundEngine;

import java.util.function.Supplier;

public class SoundPoolPlayGroup {
    private static final float DEFAULT_VOLUME = .45f;
    private SFXPlayback currently;
    private Supplier<Boolean> bindPlaySupplier = null;

    public SoundPoolPlayGroup() {
    }

    public SoundPoolPlayGroup(Supplier<Boolean> bindPlaySupplier) {
        this.bindPlaySupplier = bindPlaySupplier;
    }

    public void play(Sound sound) {
        play(sound, DEFAULT_VOLUME);
    }

    public void play(Sound sound, float volume) {
        if (Boolean.FALSE.equals(bindPlaySupplier.get())) {
            return;
        }
        if (currently != null)
            currently.cancel();
        currently = Game.audio().playSound(sound, false, SoundEngine.DEFAULT_MAX_DISTANCE, volume);
    }

}
