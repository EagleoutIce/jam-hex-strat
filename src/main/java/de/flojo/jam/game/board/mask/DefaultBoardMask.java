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
        playerOneMasks.add(new RectangleMask(0,0,1,6));
        playerOneMasks.add(new RectangleMask(0,17,1,23));
        playerOneMasks.add(new RectangleMask(10,26,11,32));
        playerOneMasks.add(new RectangleMask(10,9,11,15));
        playerOneMasks.add(new RectangleMask(2,26,3,32));
        playerOneMasks.add(new RectangleMask(2,9,3,15));
        playerOneMasks.add(new RectangleMask(4,0,5,6));
        playerOneMasks.add(new RectangleMask(4,17,5,23));
        playerOneMasks.add(new RectangleMask(6,26,7,32));
        playerOneMasks.add(new RectangleMask(6,9,7,15));
        playerOneMasks.add(new RectangleMask(8,0,9,6));
        playerOneMasks.add(new RectangleMask(8,17,9,23));
        playerTwoMasks = new HashSet<>();
        playerTwoMasks.add(new RectangleMask(0,26,1,32));
        playerTwoMasks.add(new RectangleMask(0,9,1,15));
        playerTwoMasks.add(new RectangleMask(10,17,11,23));
        playerTwoMasks.add(new RectangleMask(2,0,3,6));
        playerTwoMasks.add(new RectangleMask(2,17,3,23));
        playerTwoMasks.add(new RectangleMask(4,26,5,32));
        playerTwoMasks.add(new RectangleMask(4,9,5,15));
        playerTwoMasks.add(new RectangleMask(6,0,7,6));
        playerTwoMasks.add(new RectangleMask(6,17,7,23));
        playerTwoMasks.add(new RectangleMask(8,9,9,15));
        playerTwoMasks.add(new RectangleMask(8,26,9,32));
        playerTwoMasks.add(new RectangleMask(10 ,0,11,6));
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
