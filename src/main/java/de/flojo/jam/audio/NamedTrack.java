package de.flojo.jam.audio;

import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.SinglePlayTrack;

import java.util.Objects;

public class NamedTrack extends SinglePlayTrack {
    private final String name;
    public NamedTrack(String resourceName, String soundName) {
        super(Resources.sounds().get(resourceName));
        this.name = soundName;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NamedTrack sounds = (NamedTrack) o;
        return Objects.equals(getName(), sounds.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }
}
