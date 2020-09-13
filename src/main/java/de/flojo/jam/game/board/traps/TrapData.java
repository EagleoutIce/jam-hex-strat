package de.flojo.jam.game.board.traps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import de.gurkenlabs.litiengine.Game;

public class TrapData extends ArrayList<List<TrapTile>> {

    private static final long serialVersionUID = 4240627169938742205L;

    public TrapData(Collection<? extends List<TrapTile>> c) {
        super(c);
    }

    public TrapData(TrapTile single) {
        super(1);
        this.add(Arrays.asList(single));
    }

    public TrapData() {
    }

  
    public TrapData(int initialCapacity) {
        super(initialCapacity);
    }
    
    public Optional<TrapTile> getTrapTileAt(int x, int y) {
        if (isEmpty() || y < 0 || y >= size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return Optional.empty();
        }
        List<TrapTile> tl = get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Requested: {0}/{1} but had no terrain data", new Object[] { x, y });
            return Optional.empty();
        }
        return Optional.of(tl.get(x));
    }

    public void setTrapTileAt(int x, int y, TrapTile newType) {
        if (isEmpty() || y < 0 || y >= size()) {
            Game.log().log(Level.WARNING, "Wanted to set: {2} on {0}/{1} but not on grid!", new Object[] { x, y, newType });
            return;
        }
        List<TrapTile> tl = get(y);
        if (tl == null || tl.isEmpty() || x < 0 || x >= tl.size()) {
            Game.log().log(Level.WARNING, "Wanted to set: {2} on {0}/{1} but not on grid!", new Object[] { x, y, newType });
            return;
        }
        TrapTile old = tl.set(x, newType);
        Game.log().log(Level.INFO, "Set Tile at {0}/{1}, which was {2} to {3}", new Object[]{x, y, old, newType});
    }

    public int getWidth() {
        if(isEmpty()) 
            return 0;
        
        List<TrapTile> l = this.get(0);
        return l == null ? 0 : l.size();
    }

    public int getHeight() {
        return this.size();
    }
}