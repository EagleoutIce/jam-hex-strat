package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.share.util.IAmJson;

// serializer is not really working as it has to be embedded in the list etc.
public class CreatureJson implements IAmJson {
    
    private static final long serialVersionUID = 2384082420599588581L;

    private String name;
    private CreatureId id;
    private BoardCoordinate pos;
    private PlayerId owner;

    public CreatureJson(final Creature base) {
        this.name = base.getName();
        this.id = base.getCreatureId();
        this.pos = base.getCoordinate();
        this.owner = base.getOwner();
    }

    public String getName() {
        return name;
    }

    public CreatureId getId() {
        return id;
    }

    public BoardCoordinate getPos() {
        return pos;
    }

    public PlayerId getOwner() {
        return owner;
    }
}
