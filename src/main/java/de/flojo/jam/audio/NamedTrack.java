package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.SinglePlayTrack;

public class NamedTrack extends SinglePlayTrack {
    private final String name;
    public NamedTrack(String resourceName, String soundName) {
        super(Resources.sounds().get(resourceName));
        this.name = soundName;
    }

    public String getName() {
        return name;
    }
}
