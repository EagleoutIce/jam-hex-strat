package de.flojo.jam.game.board.terrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainData;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.resources.Resources;

public class TerrainMap implements Serializable {

    private static final long serialVersionUID = 4299557680869120087L;

    private Terrain terrain;

    private transient Gson gson = new GsonBuilder().create();

    public TerrainMap(int w, int h, String terrainPath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.get(terrainPath)))) {
            terrain = gson.fromJson(reader, Terrain.class);
        } catch (IOException | JsonSyntaxException | JsonIOException ex) {
            Game.log().warning(ex.getMessage());
            System.exit(1);
        }
        Game.log().log(Level.INFO, "Loaded: {0}", terrain);
    }

    public TerrainType getTerrainAt(int x, int y) {
        TerrainData t = terrain.getData();
        if (t == null) {
            return TerrainType.EMPTY;
        } else {
            return t.getTerrainAt(x, y);
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

}
