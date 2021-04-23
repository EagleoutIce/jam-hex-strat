package de.flojo.jam.networking.server;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.INeedUpdates;
import de.flojo.jam.networking.NetworkGson;
import de.flojo.jam.networking.exceptions.ErrorTypeEnum;
import de.flojo.jam.networking.exceptions.HandlerException;
import de.flojo.jam.networking.exceptions.IllegalMessageException;
import de.flojo.jam.networking.exceptions.NameNotAvailableException;
import de.flojo.jam.networking.messages.BuildChoiceMessage;
import de.flojo.jam.networking.messages.BuildPhaseStartMessage;
import de.flojo.jam.networking.messages.BuildUpdateMessage;
import de.flojo.jam.networking.messages.ErrorMessage;
import de.flojo.jam.networking.messages.GameOverMessage;
import de.flojo.jam.networking.messages.HelloMessage;
import de.flojo.jam.networking.messages.HelloReplyMessage;
import de.flojo.jam.networking.messages.MessageContainer;
import de.flojo.jam.networking.messages.MessageTypeEnum;
import de.flojo.jam.networking.messages.TurnActionMessage;
import de.flojo.jam.networking.server.management.MainGameControl;
import de.flojo.jam.networking.server.management.ServerStateEnum;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.IProvideContext;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ServerController implements IServerController {

    protected final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final ServerSocket socket;
    private final PlayerController playerController;
    private final MainGameControl mGController;
    private final INeedUpdates<String> networkUpdateTarget;

    private ServerStateEnum state = ServerStateEnum.OFF;

    public ServerController(final InetSocketAddress address, final IProvideContext context,
                            final INeedUpdates<String> networkUpdateTarget, int startMoney) {
        socket = new ServerSocket(address, this);
        playerController = new PlayerController();
        mGController = new MainGameControl(this, context, startMoney);
        this.networkUpdateTarget = networkUpdateTarget;
    }

    @Override
    public void handleCloseFor(final WebSocket conn, final int code, final String reason, final boolean remote) {
        HexStratLogger.log().log(Level.INFO, "Closed connection with {0} with code {1} and reason {2} (remote: {3})",
                new Object[]{conn, code, reason, remote});
        executorService.execute(() -> {
            playerController.removePlayer(conn);
            if (state != ServerStateEnum.WAITING_FOR_PLAYERS) {
                networkUpdateTarget.call("STOPPED", "Player left in game");
            }
        });
    }

    @Override
    public void handleMessage(final WebSocket conn, final String message) {
        HexStratLogger.log().log(Level.INFO, "Got Message: \"{0}\" from {1}", new Object[]{message, conn});
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

    private void processMessage(final WebSocket conn, final String message) {
        final ClientServerConnection connection = conn.getAttachment();
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
                case BUILD_CHOICE:
                    handleBuildChoice(NetworkGson.getMessage(message), connection);
                    break;
                case TURN_ACTION:
                    handleTurnAction(NetworkGson.getMessage(message), connection);
                    break;
                default:
                    throw new IllegalMessageException("There was no handler for: " + type + " (" + message + ")");
            }

        } catch (final HandlerException ex) {
            sendErrorMessageToDealWithHandlerException(conn, ex);
        }
    }

    private void handleBuildChoice(final BuildChoiceMessage message,
                                   final ClientServerConnection connection) {
        HexStratLogger.log().log(Level.INFO, "Received build choice from {0}: {1}", new Object[]{connection, message});
        if (message.isGift()) {
            mGController.giftRestOfMoney(connection.getRole());
        } else if (message.getTerrain() != null) {
            // place terrain
            mGController.buildTerrainAt(connection.getRole(), message.getTerrain(), message.getPosition());
        } else if (message.getCreature() != null) {
            mGController.summonCreatureAt(connection.getRole(), message.getCreature(), message.getPosition());
        } else if (message.getTrap() != null) {
            mGController.spawnTrapAt(connection.getRole(), message.getTrap(), message.getPosition());
        } else {
            HexStratLogger.log().log(Level.SEVERE, "No build-choice from {0}: {1}.", new Object[]{connection, message});
        }

        playerController.sendBoth(new BuildUpdateMessage(null, mGController.getTerrainMap()));

        if (!mGController.nextBuildRequest()) {
            mGController.startMainGame();
        }
    }

    private void handleTurnAction(final TurnActionMessage message,
                                  final ClientServerConnection connection) {
        HexStratLogger.log().log(Level.INFO, "Received turn action from {0}: {1}", new Object[]{connection, message});
        if (connection == null)
            return;
        mGController.performAction(message);
        // Maybe make a new message to avoid wrong sending?
        // or introduce a private signature?
        playerController.getOtherPlayer(connection.getRole()).send(message);
        if (!mGController.nextGameAction())
            gameOver();
    }

    private void handleHello(final HelloMessage message, final WebSocket conn,
                             final ClientServerConnection csConnection) throws IllegalMessageException, NameNotAvailableException {
        HexStratLogger.log().log(Level.INFO, "Received hello: {0}", message);
        if (csConnection != null)
            throw new IllegalMessageException("You have already sent a Hello-Message");

        final var newConnection = new ClientServerConnection(conn, message);
        playerController.addPlayer(newConnection);
        conn.setAttachment(newConnection);
        conn.send(new HelloReplyMessage(newConnection, mGController.getTerrain()).toJson());
        if (playerController.ready()) {
            playerController.sendBoth(new BuildPhaseStartMessage(null, playerController));
            startGame();
        }
    }

    private void startGame() {
        this.state = ServerStateEnum.BUILD_PHASE;
        // send request
        mGController.startBuildPhase();
    }

    private void handleNullTypeOnContainer(final WebSocket conn, final ClientServerConnection connection,
                                           final String message) {
        HexStratLogger.log().log(Level.WARNING, "The Message: \"{0}\" was not in a valid container format!", message);
        UUID servedByClientId;
        if (connection == null) {
            servedByClientId = null;
        } else {
            servedByClientId = connection.getClientId();
        }

        final var error = new ErrorMessage(servedByClientId, ErrorTypeEnum.ILLEGAL_MESSAGE,
                "The Message you send was not in a valid container format!");
        conn.send(error.toJson());
        conn.close(CloseFrame.REFUSE);
    }

    private void sendErrorMessageToDealWithHandlerException(final WebSocket conn, final HandlerException ex) {
        HexStratLogger.log().log(Level.SEVERE, "Error while handling: {0} ({1}).",
                new Object[]{ex.getError(), ex.getMessage()});
        final ClientServerConnection connection = conn.getAttachment();
        UUID servedByClientId;
        if (connection == null) { // Bounce-Back to sender!
            servedByClientId = null;
        } else {
            servedByClientId = connection.getClientId();
        }
        final var error = new ErrorMessage(servedByClientId, ex.getError(), ex.getMessage());
        conn.send(error.toJson());
        conn.close(CloseFrame.REFUSE);
    }

    @Override
    public void handleOpenFor(final WebSocket conn) {
        HexStratLogger.log().log(Level.INFO, "Connected: {0}", new Object[]{conn});
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public boolean isReady() {
        return socket.isReady();
    }

    public String socketInfo() {
        return socket.socketInfo();
    }

    public String playerInfo() {
        return playerController.getInfo();
    }

    public void start() {
        socket.start();
        state = ServerStateEnum.WAITING_FOR_PLAYERS;
    }

    public void gameOver() {
        // analyze winner
        final PlayerId winner = mGController.getWinner().orElse(null);
        // send
        sendGameOver(winner);
        // ?stop change?
    }

    public void sendGameOver(final PlayerId winnerId) {
        final var gameOverMessage = new GameOverMessage(null, winnerId);
        playerController.sendBoth(gameOverMessage);
    }

    public void stop() {
        try {
            socket.stop();
            state = ServerStateEnum.OFF;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void handleError(final WebSocket conn, final Exception ex) {
        networkUpdateTarget.call("STOPPED", ex.getMessage());
    }


}
