package de.flojo.jam.networking.messages;

import java.util.Objects;
import java.util.UUID;

import de.flojo.jam.game.board.terrain.management.Terrain;

public class HelloReplyMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private UUID sessionId;
    private Terrain terrain;

    public HelloReplyMessage(UUID clientId, UUID sessionId, Terrain terrain) {
        this(clientId, sessionId, terrain, "");
    }

    public HelloReplyMessage(UUID clientId, UUID sessionId, Terrain terrain, String debugMessage) {
        super(MessageTypeEnum.HELLO, clientId, debugMessage);
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public String toString() {
        return "HelloReplyMessage [<container>=" + super.toString() + ", terrain=" + this.terrain + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(sessionId, terrain);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

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
                && super.equals(obj);
    }

}