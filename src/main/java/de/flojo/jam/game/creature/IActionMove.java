package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;

import java.util.List;

@FunctionalInterface
public interface IActionMove {
    void onMove(BoardCoordinate creaturePosition, List<BoardCoordinate> targets);
}
