package de.flojo.jam.game.terrain;

import java.io.Serializable;
import java.util.List;

public class Terrain implements Serializable {

    private static final long serialVersionUID = 8198115689285126822L;

    private final String name;
    private final List<List<TerrainType>> data;

    public String getName() {
        return name;
    }


    public List<List<TerrainType>> getData() {
        return data;
    }

    public Terrain(String name, List<List<TerrainType>> terrain) {
        this.name = name;
        this.data = terrain;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Terrain [data=").append(data).append(", name=").append(name).append("]");
        return builder.toString();
    }

}
