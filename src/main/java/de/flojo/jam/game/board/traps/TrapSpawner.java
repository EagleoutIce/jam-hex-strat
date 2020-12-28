package de.flojo.jam.game.board.traps;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;

public class TrapSpawner {

	private final Board board;

	private TrapCollection traps;

	private Trap selectedTrap;

	public TrapSpawner(final Board board, final String screen){
		this.board = board;
		this.traps = new TrapCollection();
		InputController.get().onClicked(this::setActiveTrap, screen);
	}

	private void setActiveTrap(MouseEvent c) {
		if (c.getButton() != MouseEvent.BUTTON1)
			return;


		Trap oldTrap = selectedTrap;
		this.selectedTrap = traps.getHighlighted().orElse(null);
		if(oldTrap != selectedTrap)
			Game.log().log(Level.INFO, "Selected Trap: {0}.", this.selectedTrap);
	}


	public Trap spawnTrap(TrapId id, PlayerId owner, Tile tile) {
		if(traps.get(tile.getCoordinate()).isPresent())
			return null;
		Trap trap =  new Trap(board, owner, Objects.requireNonNull(id, "Cannot spawn trap without an id (TrapId)"), tile);
		traps.add(trap);
		Game.log().log(Level.INFO, "Spawned trap with id \"{0}\" at {1} with Id \"{2}\"", new Object[] {id, tile, owner});
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

	public boolean canBePlaced(CreatureFactory creatures, TrapId id, Tile pos, PlayerId playerId, Board board) {
		Set<Tile> tiles = Trap.getEffectiveTiles(id.getImprint(), pos, board);
		for (Tile tile : tiles) {
			if (tile == null)
				return false;
			if(playerId != null && tile.getPlacementOwner() != playerId)
				return false;
		}
		return traps.getCollision(tiles).isEmpty() && creatures.get(tiles).isEmpty();
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

	public void updateTraps(List<TrapJson> nT) {
		traps.clear();
		for (TrapJson tJ : nT) {
			Trap deserialized = new Trap(board, tJ.getOwner(), tJ.getId(), board.getTile(tJ.getPos()));
			traps.add(deserialized);
		}
	}
}