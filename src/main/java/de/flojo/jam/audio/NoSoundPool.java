package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.sound.Sound;

import java.util.Optional;

public class NoSoundPool implements SoundPool<Sound> {

    private static final SoundPoolPlayGroup group = new DummySoundPoolPlayGroup();

    @Override
    public Optional<Sound> get() {
        return Optional.empty();
    }

    @Override
    public SoundPoolPlayGroup getGroup() {
        return group;
    }
}
