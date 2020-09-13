package de.flojo.jam.networking.client;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import de.flojo.jam.networking.exceptions.NoConnectionException;
import de.flojo.jam.networking.messages.HelloMessage;
import de.flojo.jam.networking.messages.MessageContainer;
import de.gurkenlabs.litiengine.Game;

public class ClientController implements IClientController {

    private static final int MAX_WAIT_INTERVAL = 5;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private final Object readyLock = new Object();
    private boolean isReady;

    private ClientSocket socket;

    public ClientController(URI serverUri) {
        super();
        this.socket = new ClientSocket(serverUri, this);
    }

    public boolean tryConnect() {
        try {
            return tryConnectRun();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean tryConnectRun() throws InterruptedException {
        this.socket.connect();
        synchronized (readyLock) {
            for (int attempts = 1; !isReady && attempts <= MAX_WAIT_INTERVAL; attempts++) {
                Game.log().log(Level.WARNING, "Waiting for a connection... Refreshing in 1s ({0}/{1})", new Object[] {attempts, MAX_WAIT_INTERVAL});
                readyLock.wait(500); // wait for it to be ready
            }
            if (!isReady) {
                throw new NoConnectionException(MAX_WAIT_INTERVAL);
            }
            return true;
        }
    }


    @Override
    public void handleOpen() {
        socket.send(new HelloMessage("Josef").toJson());
        synchronized (readyLock) {
            isReady = true;
            readyLock.notifyAll();
        }
    }

    @Override
    public void handleClose(int code, String reason, boolean remote) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessage(String message) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * @see org.java_websocket.client.WebSocketClient#close()
     */

    public void close() {
        socket.close();
    }

    @Override
    public void send(MessageContainer message) {
        socket.send(message.toJson());
    }

    
}
