package de.flojo.jam.game.board.traps;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.share.util.IAmJson;

public class TrapJson implements IAmJson {
    
    private TrapId id;
    private BoardCoordinate pos;
    private PlayerId owner;
    private boolean uncovered;

    public TrapJson(final Trap trap){
        this.id = trap.getTrapId();
        this.pos = trap.getCoordinate();
        this.owner = trap.getOwner();
        this.uncovered = trap.uncoveredByEnemy();
    }

    public TrapId getId() {
        return id;
    }

    public PlayerId getOwner() {
        return owner;
    }

    public BoardCoordinate getPos() {
        return pos;
    }

    public boolean isUncovered() {
        return uncovered;
    }

}
