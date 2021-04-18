package de.flojo.jam.networking.messages;

import java.util.UUID;

public class NextRoundMessage extends MessageContainer {

    private static final long serialVersionUID = -7249229150894475012L;

    private final int currentRound;

    public NextRoundMessage(UUID clientId, int currentRound) {
        this(clientId, currentRound, "");
    }

    public NextRoundMessage(UUID clientId, int currentRound, String debugMessage) {
        super(MessageTypeEnum.NEXT_ROUND, clientId, debugMessage);
        this.currentRound = currentRound;
    }

    public int getCurrentRound() {
        return currentRound;
    }
}