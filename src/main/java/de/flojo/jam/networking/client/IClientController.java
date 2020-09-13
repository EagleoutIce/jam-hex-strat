package de.flojo.jam.networking.client;

import de.flojo.jam.networking.messages.MessageContainer;

public interface IClientController {
    
    void handleOpen();

    void handleClose(int code, String reason, boolean remote);

    void handleMessage(String message);

    // TODO: split to sender?
    void send(MessageContainer message);
}
