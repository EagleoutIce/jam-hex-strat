package de.flojo.jam.game.board.terrain.management;

import java.io.Serializable;

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

}
