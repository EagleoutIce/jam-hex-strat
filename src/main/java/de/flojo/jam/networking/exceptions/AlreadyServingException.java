package de.flojo.jam.networking.exceptions;

public class AlreadyServingException extends HandlerException {

    private static final long serialVersionUID = -4814730436260537119L;

    public AlreadyServingException(String message) {
        super(message, ErrorTypeEnum.ALREADY_SERVING);
    }
}