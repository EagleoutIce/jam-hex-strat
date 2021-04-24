package de.flojo.jam.networking.client;

import de.flojo.jam.graphics.INeedUpdates;
import de.flojo.jam.networking.NetworkGson;
import de.flojo.jam.networking.messages.ErrorMessage;
import de.flojo.jam.networking.messages.MessageContainer;
import de.flojo.jam.networking.messages.MessageTypeEnum;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.HexStratLogger;

import javax.swing.JOptionPane;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ClientController implements IClientController {

    private static final int MAX_WAIT_INTERVAL = 5;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private final Object readyLock = new Object();
    private final ClientSender sender;
    private final ClientSocket socket;
    private final ClientContext context;
    boolean connected = false;
    private boolean isReady;
    private INeedUpdates<String> onConnectionStateUpdate;

    // NOTE: We do not care about the state update safety... cause i have time
    // problems :D
    public ClientController(URI serverUri, INeedUpdates<String> onConnectionStateUpdate) {
        super();
        this.socket = new ClientSocket(serverUri, this);
        this.sender = new ClientSender(this);
        this.context = new ClientContext();
        this.onConnectionStateUpdate = onConnectionStateUpdate;
    }

    @SuppressWarnings("java:S2274")
    public void tryConnect(Consumer<Boolean> onCompleted) {
        this.socket.connect();
        new Thread(() -> {
            synchronized (readyLock) {
                for (var attempts = 1; !isReady && attempts <= MAX_WAIT_INTERVAL; attempts++) {
                    HexStratLogger.log().log(Level.WARNING, "Waiting for a connection... Refreshing in 1s ({0}/{1})",
                                             new Object[]{attempts, MAX_WAIT_INTERVAL});
                    try {
                        readyLock.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
                if (!isReady) {
                    HexStratLogger.log().log(Level.SEVERE, "Was not able to establish a connection. Waited {0} times.",
                                             MAX_WAIT_INTERVAL);
                }
                onCompleted.accept(isReady);
            }
        }).start();
    }


    @Override
    public void handleOpen() {
        synchronized (readyLock) {
            isReady = true;
            connected = true;
            readyLock.notifyAll();
        }
    }

    @Override
    public void handleClose(int code, String reason, boolean remote) {
        HexStratLogger.log().log(Level.INFO, "Connection closed for {0} with reason \"{1}\". Remote: {2}",
                                 new Object[]{code, reason, remote});
        connected = false;
        // do nothing else?
        if (onConnectionStateUpdate != null)
            onConnectionStateUpdate.call(new String[]{"CLOSED"});
    }

    @Override
    public void handleMessage(String message) {
        executorService.execute(() -> processMessage(message));
    }

    private void processMessage(String message) {
        HexStratLogger.log().log(Level.INFO, "Handling message: \"{0}\"", message);

        final MessageContainer container = NetworkGson.getContainer(message);
        final MessageTypeEnum type = container == null ? null : container.getType();
        if (type == null) {
            HexStratLogger.log().warning("Type was null; ignoring...");
            return;
        }

        // we do not care if double received, i have time problems anyways :C :D
        switch (type) {
            case HELLO_REPLY:
                context.handleHelloReply(NetworkGson.getMessage(message));
                break;
            case BUILD_START:
                context.handleGameStart(NetworkGson.getMessage(message));
                if (onConnectionStateUpdate != null)
                    onConnectionStateUpdate.call(new String[]{"START"});
                break;
            case YOU_CAN_BUILD:
                GameScreen.get().buildOne(NetworkGson.getMessage(message));
                break;
            case BUILD_UPDATE:
                GameScreen.get().updateMap(NetworkGson.getMessage(message));
                break;
            // For mirroring, may be changed
            case TURN_ACTION:
                GameScreen.get().performTurn(NetworkGson.getMessage(message));
                break;
            case NEXT_ROUND:
                GameScreen.get().nextRound(NetworkGson.getMessage(message));
                break;
            case YOUR_TURN:
                GameScreen.get().ourTurn(NetworkGson.getMessage(message));
                break;
            case GAME_START:
                GameScreen.get().initGameStart(NetworkGson.getMessage(message));
                break;
            case GAME_OVER:
                GameScreen.get().gameOver(NetworkGson.getMessage(message));
                break;
            case ERROR:
                handleError(NetworkGson.getMessage(message));
                break;
            default:
                HexStratLogger.log().log(Level.WARNING, "There was no handler for: {0} ({1}).",
                                         new Object[]{type, message});
        }
    }

    private void handleError(final ErrorMessage message) {
        JOptionPane.showMessageDialog(null,
                                      message.getReason().getDescription() + " (" + message.getDebugMessage() + ")",
                                      "Received Error " + message.getReason() , JOptionPane.ERROR_MESSAGE);
    }


    public void setOnConnectionStateUpdate(INeedUpdates<String> onConnectionStateUpdate) {
        this.onConnectionStateUpdate = onConnectionStateUpdate;
    }

    public void close() {
        socket.close();
        connected = false;
    }

    public ClientSender getSender() {
        return sender;
    }

    @Override
    public void send(MessageContainer message) {
        message.setClientId(context.getMyId());
        HexStratLogger.log().log(Level.INFO, "Sending: {0}", message.toJson());
        socket.send(message.toJson());
    }

    @Override
    public ClientContext getContext() {
        return context;
    }

    public String getConnectedStatus() {
        if (socket != null && socket.getRemoteSocketAddress() != null)
            return socket.getRemoteSocketAddress().toString();
        return "Not connected";
    }

    public boolean isConnected() {
        return connected;
    }

}
