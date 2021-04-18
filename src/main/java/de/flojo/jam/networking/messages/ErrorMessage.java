package de.flojo.jam.networking.messages;

import de.flojo.jam.networking.exceptions.ErrorTypeEnum;

import java.util.UUID;

public class ErrorMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private ErrorTypeEnum reason;

    public ErrorMessage(UUID clientId, ErrorTypeEnum reason) {
        this(clientId, reason, "");
    }

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
}