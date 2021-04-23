package de.flojo.jam.networking.messages;

import de.flojo.jam.networking.server.PlayerController;

import java.util.Objects;
import java.util.UUID;

public class BuildPhaseStartMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private final UUID p1Id;
    private final UUID p2Id;

    private final String p1Name;
    private final String p2Name;

    public BuildPhaseStartMessage(UUID clientId, PlayerController c) {
        this(clientId, c.getPlayerOne().getClientId(), c.getPlayerTwo().getClientId(), c.getPlayerOne().getClientName(),
             c.getPlayerTwo().getClientName(), "");
    }

    public BuildPhaseStartMessage(UUID clientId, UUID p1Id, UUID p2Id, String p1Name, String p2Name,
                                  String debugMessage) {
        super(MessageTypeEnum.BUILD_START, clientId, debugMessage);
        this.p1Id = p1Id;
        this.p2Id = p2Id;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(p1Id, p1Name, p2Id, p2Name);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BuildPhaseStartMessage)) {
            return false;
        }
        BuildPhaseStartMessage other = (BuildPhaseStartMessage) obj;
        return Objects.equals(p1Id, other.p1Id) && Objects.equals(p1Name, other.p1Name)
                && Objects.equals(p2Id, other.p2Id) && Objects.equals(p2Name, other.p2Name);
    }


    @Override
    public String toString() {
        return "GameStartMessage [<container>=" + super.toString() + ", p1Id=" + p1Id + ", p1Name=" + p1Name + ", p2Id=" +
                p2Id + ", p2Name=" + p2Name + "]";
    }


    public UUID getP1Id() {
        return p1Id;
    }

    public UUID getP2Id() {
        return p2Id;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }


}