package de.flojo.jam.game.board.traps;

import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;

public class TrapSpawner {
    
    private final Board board;

    private TrapCollection traps;

    private Trap selectedTrap;

    public TrapSpawner(final Board board){
        this.board = board; 
        this.traps = new TrapCollection();
        Input.mouse().onClicked(this::setActiveTrap);
    }

    private void setActiveTrap(MouseEvent c) {
        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        this.selectedTrap = traps.getHighlighted().orElse(null);
        Game.log().log(Level.INFO, "Selected Trap: {0}.", this.selectedTrap);
    }


    public Trap spawnTrap(TrapId id, PlayerId owner, Tile tile) {
        Trap trap =  new Trap(board, owner, Objects.requireNonNull(id, "Cannot spawn trap without an id (TrapId)"), tile);
        traps.add(trap);
        return trap;
    }

    public Trap getSelectedTrap() {
        return selectedTrap;
    }

    public void removeTrap(Tile onBase) {
        traps.removeIf(t -> t.coversTile(onBase.getCoordinate()));
    }

    public TrapCollection getTraps() {
        return traps;
    }

    public Optional<Trap> get(BoardCoordinate coordinate) {
        return traps.get(coordinate);
    }

    // only returns on root tile
    public Optional<Trap> getRoot(BoardCoordinate coordinate) {
        return traps.getRoot(coordinate);
    }

    public boolean isEmpty() {
        return traps.isEmpty();
    }

    public int size() {
        return traps.size();
    }

	public void removeAll() {
        this.traps.clear();
	}
}
