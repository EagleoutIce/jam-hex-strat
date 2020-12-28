package de.flojo.jam.game.board.mask;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.player.PlayerId;

public interface IBoardMask {
	PlayerId getOwner(BoardCoordinate coordinate);
}
