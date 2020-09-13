package de.flojo.jam.networking.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import de.gurkenlabs.litiengine.Game;

public final class ServerSocket extends WebSocketServer {

    private static final String SERVER_TEXT = "Server";

    private IServerController controller;
    protected boolean isReady;

    public boolean isReady() {
        return this.isReady();
    }

    public String socketInfo() {
        return this.getAddress() + " (" + this.getConnections().size() + " connections)";
    }

    public ServerSocket(InetSocketAddress address, IServerController controller) {
        super(address);
        this.controller = controller;
        this.isReady = false;
    }

    public ServerSocket updateController(IServerController newController) {
        this.controller = newController;
        return this;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        controller.handleCloseFor(conn, code, reason, remote);
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        Game.log().severe(ex.getMessage());
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
