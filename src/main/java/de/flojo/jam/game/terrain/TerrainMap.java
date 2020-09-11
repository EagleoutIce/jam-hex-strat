package de.flojo.jam.game.terrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

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
        List<List<TerrainType>> t = terrain.getData();
        if (t == null || t.isEmpty() || y < 0 || y >= t.size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but no had no terrain data", new Object[] { x, y });
            return TerrainType.EMPTY;
        }
        List<TerrainType> tl = t.get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but no had no terrain data", new Object[] { x, y });
            return TerrainType.EMPTY;
        }
        return tl.get(x);
    }

    public Terrain getTerrain() {
        return terrain;
    }


}
