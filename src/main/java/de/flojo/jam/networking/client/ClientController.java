package de.flojo.jam.networking.client;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import de.flojo.jam.graphics.INeedUpdates;
import de.flojo.jam.networking.NetworkGson;
import de.flojo.jam.networking.exceptions.NoConnectionException;
import de.flojo.jam.networking.messages.MessageContainer;
import de.flojo.jam.networking.messages.MessageTypeEnum;
import de.gurkenlabs.litiengine.Game;

public class ClientController implements IClientController {

    private static final int MAX_WAIT_INTERVAL = 5;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private final Object readyLock = new Object();
    private boolean isReady;

    private final ClientSender sender;
    private final ClientSocket socket;
    private final ClientContext context;
    private final INeedUpdates<String> onConnectionStateUpdate;
    

    // NOTE: We do not care about the state update safety... cause i have time problems :D
    public ClientController(URI serverUri, INeedUpdates<String> onConnectionStateUpdate) {
        super();
        this.socket = new ClientSocket(serverUri, this);
        this.sender = new ClientSender(this);
        this.context = new ClientContext();
        this.onConnectionStateUpdate = onConnectionStateUpdate;
    }

    public boolean tryConnect() {
        try {
            return tryConnectRun();
        } catch (InterruptedException | NoConnectionException  e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean tryConnectRun() throws InterruptedException, NoConnectionException {
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
        synchronized (readyLock) {
            isReady = true;
            readyLock.notifyAll();
        }
    }

    @Override
    public void handleClose(int code, String reason, boolean remote) {
        Game.log().log(Level.INFO, "Connection closed for {0} with reason \"{1}\". Remote: {2}", new Object[] {code, reason, remote});
        // do nothing else?
        onConnectionStateUpdate.call("CLOSED");
    }

    @Override
    public void handleMessage(String message) {
        executorService.execute(() -> processMessage(message));
    }

    private void processMessage(String message) {
        Game.log().log(Level.INFO, "Handling message: \"{0}\"", message);

        final MessageContainer container = NetworkGson.getContainer(message);
        final MessageTypeEnum type = container == null ? null : container.getType();
        if(type == null) {
            Game.log().warning("Type was null; ignoring...");
            return;
        }

        // we do not care if double received, i have time problems anyways :C :D
        switch(type) {
            case HELLO_REPLY:
                context.handleHelloReply(NetworkGson.getMessage(message));
                break;
            case GAME_START:
                context.handleGameStart(NetworkGson.getMessage(message));
                onConnectionStateUpdate.call("START");
                break;
            default:
                Game.log().log(Level.WARNING, "There was no handler for: {0} ({1}).", new Object[] {type, message});
        }

    }


    public void close() {
        socket.close();
    }

    public ClientSender getSender() {
        return sender;
    }

    @Override
    public void send(MessageContainer message) {
        socket.send(message.toJson());
    }

    @Override
    public ClientContext getContext() {
        return context;
    }

	public String getConnectedStatus() {
		return socket.getRemoteSocketAddress().toString();
	}

    
}
