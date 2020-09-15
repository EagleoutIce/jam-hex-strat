package de.flojo.jam.networking.messages;

import java.util.UUID;

public class YouCanBuildMessage extends MessageContainer {

    private static final long serialVersionUID = -6643166910354159927L;

    private int currentRound;

    public YouCanBuildMessage(UUID clientId, int currentRound) {
        this(clientId, currentRound, "");
    }

    public YouCanBuildMessage(UUID clientId, int currentRound, String debugMessage) {
        super(MessageTypeEnum.YOU_CAN_BUILD, clientId, debugMessage);
        this.currentRound = currentRound;
    }
    
    public int getCurrentRound() {
        return currentRound;
    }
}
