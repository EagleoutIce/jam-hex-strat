package de.flojo.jam.networking.messages;

import de.flojo.jam.game.player.PlayerId;

import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameOverMessage that = (GameOverMessage) o;
        return getWinnerId() == that.getWinnerId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWinnerId());
    }
}
