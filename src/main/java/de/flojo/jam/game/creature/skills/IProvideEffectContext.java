package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.CreatureCollection;

public interface IProvideEffectContext {
	Board getBoard();
	CreatureCollection getCreatures();
	TrapCollection getTraps();
}
