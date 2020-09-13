package de.flojo.jam.game.board.terrain.management;

import java.io.Serializable;
import java.util.Objects;

public class Terrain implements Serializable {

    private static final long serialVersionUID = 8198115689285126822L;

    private String name;
    private final TerrainData data;

    public String getName() {
        return name;
    }

    public TerrainData getData() {
        return data;
    }

    public Terrain(String name, TerrainData terrain) {
        this.name = name;
        this.data = terrain;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Terrain [data=").append(data).append(", name=").append(name).append("]");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */

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
