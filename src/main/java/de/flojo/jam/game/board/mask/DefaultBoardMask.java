package de.flojo.jam.game.board.mask;

import java.util.HashSet;
import java.util.Set;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.player.PlayerId;

public class DefaultBoardMask implements IBoardMask {

    private final Set<RectangleMask> playerOneMasks;
    private final Set<RectangleMask> playerTwoMasks;

    private DefaultBoardMask() {
        playerOneMasks = new HashSet<>();
        playerTwoMasks = new HashSet<>();
    }

    private static final DefaultBoardMask instance = new DefaultBoardMask();

    public static final DefaultBoardMask get() {
        return instance;
    }

    // first rect: 0,0 to 1,6

    @Override
    public PlayerId getOwner(BoardCoordinate coordinate) {
        if(playerOneMasks.stream().anyMatch(r -> r.covers(coordinate.x, coordinate.y)))
            return PlayerId.ONE;
        if(playerTwoMasks.stream().anyMatch(r -> r.covers(coordinate.x, coordinate.y)))
            return PlayerId.TWO;
        return null; // none
    }




}
