package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.server.ClientServerConnection;

import java.util.Objects;
import java.util.UUID;

public class HelloReplyMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private final Terrain terrain;
    private final PlayerId role;

    public HelloReplyMessage(ClientServerConnection connection, Terrain terrain) {
        this(connection.getClientId(), terrain, connection.getRole(), "");
    }

    public HelloReplyMessage(UUID clientId, Terrain terrain, PlayerId role, String debugMessage) {
        super(MessageTypeEnum.HELLO_REPLY, clientId, debugMessage);
        this.terrain = terrain;
        this.role = role;
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
        result = prime * result + Objects.hash(terrain, role);
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
        return Objects.equals(terrain, other.terrain) && role == other.role && super.equals(obj);
    }

}