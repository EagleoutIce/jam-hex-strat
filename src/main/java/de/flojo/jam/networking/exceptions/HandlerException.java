package de.flojo.jam.networking.exceptions;

public class HandlerException extends Exception {

    private static final long serialVersionUID = -3310578299796452961L;

    private ErrorTypeEnum errorType;

    public HandlerException(String message, ErrorTypeEnum errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorTypeEnum getError() {
        return this.errorType;
    }
}