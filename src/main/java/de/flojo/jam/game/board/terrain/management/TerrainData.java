package de.flojo.jam.game.board.terrain.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import de.flojo.jam.game.board.terrain.TerrainTile;
import de.gurkenlabs.litiengine.Game;

// assert rectangle :/
public class TerrainData extends ArrayList<List<TerrainTile>> {

    private static final long serialVersionUID = 4240627169938742205L;

    public TerrainData(Collection<? extends List<TerrainTile>> c) {
        super(c);
    }

    public TerrainData(TerrainTile single) {
        super(1);
        this.add(Arrays.asList(single));
    }

    public TerrainData() {
    }

  
    public TerrainData(int initialCapacity) {
        super(initialCapacity);
    }
 
    
    public TerrainTile getTerrainAt(int x, int y) {
        if (isEmpty() || y < 0 || y >= size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return TerrainTile.EMPTY;
        }
        List<TerrainTile> tl = get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return TerrainTile.EMPTY;
        }
        return tl.get(x);
    }

    public void setTerrainAt(int x, int y, TerrainTile newType) {
        if (isEmpty() || y < 0 || y >= size()) {
            Game.log().log(Level.WARNING, "Wanted to set: {2} on {0}/{1} but not on grid!", new Object[] { x, y, newType });
            return;
        }
        List<TerrainTile> tl = get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Wanted to set: {2} on {0}/{1} but not on grid!", new Object[] { x, y, newType });
            return;
        }
        TerrainTile old = tl.set(x, newType);
        Game.log().log(Level.INFO, "Set Tile at {0}/{1}, which was {2} to {3}", new Object[]{x, y, old, newType});
    }

    public int getWidth() {
        if(isEmpty()) 
            return 0;
        
        List<TerrainTile> l = this.get(0);
        return l == null ? 0 : l.size();
    }

    public int getHeight() {
        return this.size();
    }
}
