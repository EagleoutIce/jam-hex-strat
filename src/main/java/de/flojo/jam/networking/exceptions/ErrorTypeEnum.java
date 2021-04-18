package de.flojo.jam.networking.exceptions;

public enum ErrorTypeEnum {
    NAME_NOT_AVAILABLE("The Name you wanted is already in use by another player."),
    ALREADY_SERVING("The server does already have enough players. Try to connect as a spectator instead."),
    ILLEGAL_MESSAGE("The message send was illegal, consult the debugMessage or the logfiles for further information"),
    GENERAL("A generic Error occurred, please consult the logfiles for more Information.");

    private String description;

    ErrorTypeEnum(String description) {
        this.description = description;
    }

    /**
     * @return A String describing the type of Error
     */
    public String getDescription() {
        return this.description;
    }
}

