package de.flojo.jam.networking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.java_websocket.WebSocket;

import de.gurkenlabs.litiengine.Game;

public class ServerController implements IServerController {

    protected final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private ServerSocket socket;

    public ServerController(InetSocketAddress address) {
        socket = new ServerSocket(address, this);
    }

    @Override
    public void handleCloseFor(WebSocket conn, int code, String reason, boolean remote) {
        Game.log().log(Level.INFO, "Closed connection with {0} with code {1} and reason {2} (remote: {3})",
                new Object[] { conn, code, reason, remote });
    }

    @Override
    public void handleCallFor(WebSocket conn, String message) {
        Game.log().log(Level.INFO, "Got Message: \"{0}\" from {1}", new Object[] { message, conn });
    }

    @Override
    public void handleOpenFor(WebSocket conn) {
        Game.log().log(Level.INFO, "Connected: {0}", new Object[] { conn });
    }

    public boolean isReady() {
        return socket.isReady();
    }

    public String socketInfo() {
        return socket.socketInfo();
    }

    public void start() {
        socket.start();
    }

    public void stop() {
        try {
            socket.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
	}
    
}
