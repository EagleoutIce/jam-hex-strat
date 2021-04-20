package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.sound.SFXPlayback;
import de.gurkenlabs.litiengine.sound.Sound;

public class SoundPoolPlayGroup {
    private SFXPlayback currently;

    public void play(Sound sound) {
        if(currently != null)
            currently.cancel();
        currently = Game.audio().playSound(sound);
    }

}
