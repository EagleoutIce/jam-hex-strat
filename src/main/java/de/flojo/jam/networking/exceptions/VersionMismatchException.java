package de.flojo.jam.networking.exceptions;


import de.flojo.jam.Main;

public class VersionMismatchException extends HandlerException {

    private static final long serialVersionUID = 5692162166830810314L;

    public VersionMismatchException(int otherVersion) {
        super("We: " + Main.getVersion() + "; They: " + otherVersion, ErrorTypeEnum.VERSION_MISMATCH);
    }
}
