package de.flojo.jam.networking.server;

import org.java_websocket.WebSocket;

public interface IServerController {
    void handleCloseFor(WebSocket conn, int code, String reason, boolean remote);

    void handleCallFor(WebSocket conn, String message);

    void handleOpenFor(WebSocket conn);
}