package de.flojo.jam.networking.exceptions;


public class IllegalMessageException extends HandlerException {

    private static final long serialVersionUID = 5692162166830810314L;

    public IllegalMessageException(String message) {
        super(message, ErrorTypeEnum.ILLEGAL_MESSAGE);
    }
}