package de.flojo.jam.game.board.terrain.management;

import java.io.Serializable;
import java.util.Objects;

public class Terrain implements Serializable {

    private static final long serialVersionUID = 8198115689285126822L;
    protected TerrainData data;
    private String name;

    public Terrain(String name, TerrainData terrain) {
        this.name = name;
        this.data = terrain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TerrainData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Terrain [data=" + data + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Terrain)) {
            return false;
        }
        Terrain other = (Terrain) obj;
        return Objects.equals(data, other.data) && Objects.equals(name, other.name);
    }

}
