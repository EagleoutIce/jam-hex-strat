package de.flojo.jam.networking.messages;

import de.flojo.jam.Main;
import de.flojo.jam.networking.share.util.IAmJson;

import java.util.Objects;
import java.util.UUID;

public class MessageContainer implements IAmJson {

    private static final long serialVersionUID = 45732886383952065L;
    private final MessageTypeEnum type;
    private final long epoch;
    private final String debugMessage;
    private final int version;
    private UUID clientId;


    private MessageContainer(MessageTypeEnum type, UUID clientId, long epoch, String debugMessage) {
        this.clientId = clientId;
        this.type = type;
        this.epoch = epoch;
        this.debugMessage = debugMessage;
        this.version = Main.getVersion();
    }

    public MessageContainer(MessageTypeEnum type, UUID clientId, String debugMessage) {
        this(type, clientId, System.nanoTime(), debugMessage);
    }

    public MessageContainer(MessageTypeEnum type, UUID clientId) {
        this(type, clientId, "");
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public MessageTypeEnum getType() {
        return type;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, debugMessage, type);
    }

    /**
     * Equals conflicting with contract as it does ignore the date comparison -- the standard locks it down to
     * seconds-precision and we do not care about it.
     *
     * @param obj Obj to compare to
     *
     * @return Equality-Relation with weakened contract
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MessageContainer)) {
            return false;
        }
        MessageContainer other = (MessageContainer) obj;

        return Objects.equals(clientId, other.clientId)// && Objects.equals(creationDate, other.creationDate)
                && Objects.equals(debugMessage, other.debugMessage) && type == other.type;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "MessageContainer{" +
                "type=" + type +
                ", epoch=" + epoch +
                ", debugMessage='" + debugMessage + '\'' +
                ", version=" + version +
                ", clientId=" + clientId +
                '}';
    }
}
