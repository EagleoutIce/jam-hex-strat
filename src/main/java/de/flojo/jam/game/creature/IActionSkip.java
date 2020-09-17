package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;

@FunctionalInterface
public interface IActionSkip {
    void process(BoardCoordinate from);
}
