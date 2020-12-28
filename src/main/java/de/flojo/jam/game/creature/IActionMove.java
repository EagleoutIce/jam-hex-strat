package de.flojo.jam.game.creature;

import java.util.List;

import de.flojo.jam.game.board.BoardCoordinate;

@FunctionalInterface
public interface IActionMove {
	void onMove(BoardCoordinate creaturePosition, List<BoardCoordinate> targets);
}
