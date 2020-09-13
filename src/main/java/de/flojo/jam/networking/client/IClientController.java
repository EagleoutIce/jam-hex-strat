package de.flojo.jam.networking.client;

public interface IClientController {
    
    void handleOpen();

    void handleClose(int code, String reason, boolean remote);

    void handleMessage(String message);
}
