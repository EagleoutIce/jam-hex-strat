package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.CreatureCollection;

public class DefaultEffectContext implements IProvideEffectContext {
	private final Board board;
	private final CreatureCollection cCollection;
	private final TrapCollection tCollection;

	public DefaultEffectContext(Board board, CreatureCollection cCollection, TrapCollection tCollection) {
		this.board = board;
		this.cCollection = cCollection;
		this.tCollection = tCollection;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public CreatureCollection getCreatures() {
		return cCollection;
	}

	@Override
	public TrapCollection getTraps() {
		return tCollection;
	}
}
