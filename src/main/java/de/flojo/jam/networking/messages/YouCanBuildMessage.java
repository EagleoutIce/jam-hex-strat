package de.flojo.jam.networking.messages;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YouCanBuildMessage that = (YouCanBuildMessage) o;
        return getMoneyLeft() == that.getMoneyLeft();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMoneyLeft());
    }
}
