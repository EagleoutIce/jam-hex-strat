package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.sound.Sound;

public class DummySoundPoolPlayGroup extends SoundPoolPlayGroup {
    @Override
    public void play(Sound sound) {
        Game.audio().playSound(sound);
    }
}
