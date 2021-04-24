package de.flojo.jam.networking.messages;

import de.flojo.jam.networking.exceptions.ErrorTypeEnum;

import java.util.Objects;
import java.util.UUID;

public class ErrorMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private final ErrorTypeEnum reason;


    public ErrorMessage(UUID clientId, ErrorTypeEnum reason, String debugMessage) {
        super(MessageTypeEnum.ERROR, clientId, debugMessage);
        this.reason = reason;
    }

    public ErrorTypeEnum getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ErrorMessage [<container>=" + super.toString() + ", reason=" + reason + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ErrorMessage that = (ErrorMessage) o;
        return getReason() == that.getReason();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getReason());
    }
}
