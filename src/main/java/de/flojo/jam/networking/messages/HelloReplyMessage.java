package de.flojo.jam.networking.messages;

import java.util.Objects;
import java.util.UUID;

import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.player.PlayerId;

public class HelloReplyMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private UUID sessionId;
    private Terrain terrain;
    private PlayerId role;

    public HelloReplyMessage(UUID clientId, UUID sessionId, Terrain terrain, PlayerId role) {
        this(clientId, sessionId, terrain, role, "");
    }

    public HelloReplyMessage(UUID clientId, UUID sessionId, Terrain terrain, PlayerId role, String debugMessage) {
        super(MessageTypeEnum.HELLO, clientId, debugMessage);
        this.sessionId = sessionId;
        this.terrain = terrain;
        this.role = role;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public String toString() {
        return "HelloReplyMessage [<container>=" + super.toString() + ", role=" + this.role + ", terrain="
                + this.terrain + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(sessionId, terrain, role);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof HelloReplyMessage))
            return false;

        HelloReplyMessage other = (HelloReplyMessage) obj;
        return Objects.equals(sessionId, other.sessionId) && Objects.equals(terrain, other.terrain)
                && role == other.role && super.equals(obj);
    }

}