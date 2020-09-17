package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;

@FunctionalInterface
public interface IActionMove {
    void process(BoardCoordinate creaturePosition, BoardCoordinate target);
}
