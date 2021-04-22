package de.flojo.jam.networking.server;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.exceptions.NameNotAvailableException;
import de.flojo.jam.networking.messages.MessageContainer;
import org.java_websocket.WebSocket;

import java.util.Objects;

public class PlayerController {

    ClientServerConnection playerOne = null;
    ClientServerConnection playerTwo = null;

    public PlayerController() {
        // do nothing?
    }

    public synchronized ClientServerConnection getConnection(WebSocket conn) {
        if (playerOne != null && Objects.equals(playerOne.getConnection(), conn)) {
            return playerOne;
        }
        if (playerTwo != null && Objects.equals(playerTwo.getConnection(), conn)) {
            return playerTwo;
        }
        return null;
    }

    public synchronized void addPlayer(ClientServerConnection connection) throws NameNotAvailableException {
        if (connection == null) return;
        if (playerOne == null) {
            playerOne = connection;
            connection.setRole(PlayerId.ONE);
        } else if (playerTwo == null) {
            if (Objects.equals(playerOne.getClientName(), connection.getClientName()))
                throw new NameNotAvailableException("Name \"" + playerOne.getClientName() + "\"");

            playerTwo = connection;
            connection.setRole(PlayerId.TWO);
        }
    }

    public void removePlayer(WebSocket connection) {
        removePlayer(getConnection(connection));
    }

    public synchronized void removePlayer(ClientServerConnection connection) {
        if (connection == null) return;
        if (Objects.equals(playerOne, connection)) {
            // shift one down
            playerOne = playerTwo;
            if (playerOne != null) {
                playerOne.setRole(PlayerId.ONE);
            }
            playerTwo = null;
        } else if (Objects.equals(playerTwo, connection)) {
            connection.setRole(null);
            playerTwo = null;
        }
    }

    public boolean ready() {
        return playerOne != null && playerTwo != null;
    }

    public int playerCount() {
        if (ready())
            return 2;
        return playerOne != null ? 1 : 0;
    }

    public ClientServerConnection getPlayerOne() {
        return playerOne;
    }

    public ClientServerConnection getPlayerTwo() {
        return playerTwo;
    }

    public void sendBoth(MessageContainer message) {
        playerOne.send(message);
        playerTwo.send(message);
    }

    public synchronized ClientServerConnection getPlayer(PlayerId owner) {
        return owner.ifOne(playerOne, playerTwo);
    }

    public synchronized ClientServerConnection getOtherPlayer(PlayerId owner) {
        return owner.ifTwo(playerOne, playerTwo);
    }

    public String playerOneName() {
        return playerOne == null ? null : playerOne.getClientName();
    }

    public String playerTwoName() {
        return playerTwo == null ? null : playerTwo.getClientName();
    }

    public String getInfo() {
        return playerTwo == null ? playerOneName() : playerOneName() + ", " + playerTwoName();
    }

}
