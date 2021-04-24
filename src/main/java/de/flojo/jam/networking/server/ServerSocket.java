package de.flojo.jam.networking.server;

import de.flojo.jam.util.HexStratLogger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public final class ServerSocket extends WebSocketServer {

    protected boolean isReady;
    private final IServerController controller;

    public ServerSocket(InetSocketAddress address, IServerController controller) {
        super(address);
        this.controller = controller;
        this.isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }

    public String socketInfo() {
        return this.getAddress() + " (" + this.getConnections().size() + " connections)";
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        controller.handleCloseFor(conn, code, reason, remote);
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        HexStratLogger.log().severe(ex.getMessage());
        controller.handleError(conn, ex);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        controller.handleMessage(conn, message);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        controller.handleOpenFor(conn);
    }

    @Override
    public void onStart() {
        this.isReady = true;
    }
}
