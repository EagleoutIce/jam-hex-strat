package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.sound.Sound;

import java.util.Optional;

public interface SoundPool<T extends Sound> {
    Optional<T> get();

    default void play() {
        get().ifPresent(getGroup()::play);
    }

    SoundPoolPlayGroup getGroup();
}
