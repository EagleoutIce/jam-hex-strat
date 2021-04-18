package de.flojo.jam.networking.messages;

import java.util.UUID;

public class YouCanBuildMessage extends MessageContainer {

    private static final long serialVersionUID = -6643166910354159927L;

    private final int moneyLeft;

    public YouCanBuildMessage(UUID clientId, int moneyLeft) {
        this(clientId, moneyLeft, "");
    }

    public YouCanBuildMessage(UUID clientId, int moneyLeft, String debugMessage) {
        super(MessageTypeEnum.YOU_CAN_BUILD, clientId, debugMessage);
        this.moneyLeft = moneyLeft;
    }

    public int getMoneyLeft() {
        return moneyLeft;
    }
}
