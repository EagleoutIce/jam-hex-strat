package de.flojo.jam.networking.messages;

import de.flojo.jam.game.player.PlayerId;

import java.util.UUID;

public class GameOverMessage extends MessageContainer {

    private static final long serialVersionUID = -7258648645550624183L;

    private final PlayerId winnerId;

    public GameOverMessage(UUID clientId, PlayerId winnerId) {
        super(MessageTypeEnum.GAME_OVER, clientId, "");
        this.winnerId = winnerId;
    }

    public PlayerId getWinnerId() {
        return winnerId;
    }

}
