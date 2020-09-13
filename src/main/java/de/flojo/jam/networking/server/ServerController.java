package de.flojo.jam.networking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.networking.NetworkGson;
import de.flojo.jam.networking.exceptions.ErrorTypeEnum;
import de.flojo.jam.networking.exceptions.HandlerException;
import de.flojo.jam.networking.exceptions.IllegalMessageException;
import de.flojo.jam.networking.exceptions.NameNotAvailableException;
import de.flojo.jam.networking.messages.ErrorMessage;
import de.flojo.jam.networking.messages.GameStartMessage;
import de.flojo.jam.networking.messages.HelloMessage;
import de.flojo.jam.networking.messages.HelloReplyMessage;
import de.flojo.jam.networking.messages.MessageContainer;
import de.flojo.jam.networking.messages.MessageTypeEnum;
import de.gurkenlabs.litiengine.Game;

public class ServerController implements IServerController {

    protected final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private ServerSocket socket;
    private PlayerController playerController;
    private final MainGameControl mainGameController;

    public ServerController(InetSocketAddress address, Board board) {
        socket = new ServerSocket(address, this);
        playerController = new PlayerController();
        mainGameController = new MainGameControl(this, board);
    }

    @Override
    public void handleCloseFor(WebSocket conn, int code, String reason, boolean remote) {
        Game.log().log(Level.INFO, "Closed connection with {0} with code {1} and reason {2} (remote: {3})",
                new Object[] { conn, code, reason, remote });
        executorService.execute(() -> playerController.removePlayer(conn));
    }

    @Override
    public void handleMessage(WebSocket conn, String message) {
        Game.log().log(Level.INFO, "Got Message: \"{0}\" from {1}", new Object[] { message, conn });
        executorService.execute(() -> processMessage(conn, message));
    }

    private MessageContainer getContainerWithUuidCheck(final String json, final ClientServerConnection connection)
            throws HandlerException {
        final MessageContainer container = NetworkGson.getContainer(json);
        if (connection != null && container != null
                && (!Objects.equals(container.getClientId(), connection.getClientId()))) {
            throw new IllegalMessageException("The uuid you sent (" + container.getClientId()
                    + ") differs from the one you got with the Hello-Reply (" + connection.getClientId()
                    + "). This is fatal and probably totally your fault.");
        }
        return container;
    }

    private void processMessage(WebSocket conn, String message) {
        ClientServerConnection connection = conn.getAttachment();
        try {
            final MessageContainer container = getContainerWithUuidCheck(message, connection);
            final MessageTypeEnum type = container == null ? null : container.getType();

            if (type == null) {
                handleNullTypeOnContainer(conn, connection, message);
                return;
            }

            switch (type) {
                case HELLO:
                    handleHello(NetworkGson.getMessage(message), conn, connection);
                    break;

                default:
                    throw new IllegalMessageException("There was no handler for: " + type + " (" + message + ")");
            }

        } catch (final HandlerException ex) {
            sendErrorMessageToDealWithHandlerException(conn, ex);
        }
    }

    private void handleHello(HelloMessage message, WebSocket conn, ClientServerConnection csConnection)
            throws IllegalMessageException, NameNotAvailableException {
        if(csConnection != null) 
            throw new IllegalMessageException("You have already sent a Hello-Message");
        
        ClientServerConnection newConnection = new ClientServerConnection(conn, message);
        playerController.addPlayer(newConnection);
        conn.send(new HelloReplyMessage(newConnection, mainGameController.getTerrain()).toJson());
        if(playerController.ready()) {
            playerController.getPlayerOne().send(new GameStartMessage(null, playerController));
            playerController.getPlayerTwo().send(new GameStartMessage(null, playerController));
        }
    }

    private void handleNullTypeOnContainer(WebSocket conn, ClientServerConnection connection, String message) {
        Game.log().log(Level.WARNING, "The Message: \"{0}\" was not in a valid Containerformat!", message);
        UUID servedByClientId;
        if (connection == null) {
            servedByClientId = null;
        } else {
            servedByClientId = connection.getClientId();
        }

        final ErrorMessage error = new ErrorMessage(servedByClientId, ErrorTypeEnum.ILLEGAL_MESSAGE,
                "The Message you send was not in a valid containerformat!");
        conn.send(error.toJson());
        conn.close(CloseFrame.REFUSE);
    }

    private void sendErrorMessageToDealWithHandlerException(final WebSocket conn, final HandlerException ex) {
        Game.log().log(Level.SEVERE, "Error while handling: {0} ({1}).", new Object[] {ex.getError(), ex.getMessage()});
        final ClientServerConnection connection = conn.getAttachment();
        UUID servedByClientId;
        if (connection == null) { // Bounce-Back to sender!
            servedByClientId = null;
        } else {
            servedByClientId = connection.getClientId();
        }
        final ErrorMessage error = new ErrorMessage(servedByClientId, ex.getError(), ex.getMessage());
        conn.send(error.toJson());
        conn.close(CloseFrame.REFUSE);
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
