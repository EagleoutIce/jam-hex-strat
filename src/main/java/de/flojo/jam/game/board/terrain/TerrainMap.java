package de.flojo.jam.game.board.terrain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainData;
import de.flojo.jam.util.HexMaths;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TerrainMap implements Serializable {

    private static final long serialVersionUID = 4299557680869120087L;
    private final transient Gson gson = new GsonBuilder().create();
    private Terrain terrain;

    public TerrainMap(Terrain terrain) {
        this.terrain = terrain;
    }

    public TerrainMap(int w, int h, String terrainPath) {
        this(w, h, terrainPath == null ? null : Resources.get(terrainPath), terrainPath);
    }

    public TerrainMap(int w, int h, InputStream stream, String terrainPath) {
        loadMapFrom(w, h, stream, terrainPath);
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeName(String name) {
        terrain.setName(name);
    }

    private void loadMapFrom(int w, int h, InputStream terrainIs, String terrainPath) {
        if (terrainIs != null) {
            loadTerrainMapFromInputStream(terrainIs);
        }
        if (terrain == null) {
            if (terrainIs != null)
                HexStratLogger.log().log(Level.WARNING, "Loading of terrain on: {0} failed and returned null",
                                         terrainPath);
            terrain = new Terrain(terrainPath == null ? "unnamed" : terrainPath, new TerrainData(h));
        }
        final TerrainData data = terrain.getData();
        // fill up
        for (var i = data.size(); i < h; i++)
            data.add(new ArrayList<>(HexMaths.effectiveWidth(w)));

        for (var y = 0; y < h; y++) {
            List<TerrainTile> line = data.get(y);
            int offset = HexMaths.effectiveWidth(w) - line.size();
            for (var x = 0; x < offset; x++) {
                line.add(TerrainTile.EMPTY);
            }
        }
        HexStratLogger.log().log(Level.INFO, "Loaded TerrainMap: {0}", terrain);
    }

    private void loadTerrainMapFromInputStream(final InputStream terrainIs) {
        try (final var reader = new BufferedReader(new InputStreamReader(terrainIs))) {
            terrain = gson.fromJson(reader, Terrain.class);
        } catch (IOException | JsonSyntaxException | JsonIOException ex) {
            HexStratLogger.log().warning(ex.getMessage());
            System.exit(1);
        }
    }

    public TerrainTile getTerrainAt(int x, int y) {
        TerrainData t = terrain.getData();
        if (t == null) {
            HexStratLogger.log().warning("Requested nonexistent terrain.");
            return TerrainTile.EMPTY;
        } else {
            return t.getTerrainAt(x, y);
        }
    }

    public void updateTerrainAt(Point p, TerrainTile newType) {
        updateTerrainAt(p.x, p.y, newType);
    }

    public void updateTerrainAt(int x, int y, TerrainTile newType) {
        TerrainData t = terrain.getData();
        if (t == null) {
            HexStratLogger.log().warning("Requested nonexistent terrain.");
        } else {
            t.setTerrainAt(x, y, newType);
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

}
