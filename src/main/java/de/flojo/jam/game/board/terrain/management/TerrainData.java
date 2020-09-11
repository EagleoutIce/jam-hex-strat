package de.flojo.jam.game.board.terrain.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import de.flojo.jam.game.board.terrain.TerrainType;
import de.gurkenlabs.litiengine.Game;

// assert rectangle :/
public class TerrainData extends ArrayList<List<TerrainType>> {

    private static final long serialVersionUID = 4240627169938742205L;

    public TerrainData(Collection<? extends List<TerrainType>> c) {
        super(c);
    }

    public TerrainData(TerrainType single) {
        super(1);
        this.add(Arrays.asList(single));
    }

    public TerrainData() {
    }

  
    public TerrainData(int initialCapacity) {
        super(initialCapacity);
    }
 
    
    public TerrainType getTerrainAt(int x, int y) {
        if (isEmpty() || y < 0 || y >= size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return TerrainType.EMPTY;
        }
        List<TerrainType> tl = get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return TerrainType.EMPTY;
        }
        return tl.get(x);
    }

    public int getWidth() {
        if(isEmpty()) 
            return 0;
        
        List<TerrainType> l = this.get(0);
        return l == null ? 0 : l.size();
    }

    public int getHeight() {
        return this.size();
    }
}
