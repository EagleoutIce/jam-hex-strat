package de.flojo.jam.networking.client;

import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;

public class ClientSocket extends WebSocketClient {

    private final IClientController controller;

    public ClientSocket(URI serverUri, IClientController controller) {
        super(serverUri);
        this.controller = controller;
        HexStratLogger.log().log(Level.INFO, "Starting client on uri: {0}", serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        controller.handleOpen();
    }

    @Override
    public void onMessage(String message) {
        controller.handleMessage(message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        controller.handleClose(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        HexStratLogger.log().severe(ex.getMessage());
    }

}
