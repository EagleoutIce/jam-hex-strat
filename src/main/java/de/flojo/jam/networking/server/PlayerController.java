package de.flojo.jam.networking.server;

import java.util.Objects;

import org.java_websocket.WebSocket;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.exceptions.NameNotAvailableException;

public class PlayerController {

    ClientServerConnection playerOne = null;
    ClientServerConnection playerTwo = null;

    public PlayerController() {
        // do nothing?
    }

    public ClientServerConnection getConnection(WebSocket conn) {
        if(playerOne != null && Objects.equals(playerOne.getConnection(),conn)) {
            return playerOne;
        }
        if(playerTwo != null && Objects.equals(playerTwo.getConnection(),conn)) {
            return playerTwo;
        }
        return null;
    }

    public boolean addPlayer(ClientServerConnection connection) throws NameNotAvailableException {
        if(connection == null) return false;
        if (playerOne == null) {
            playerOne = connection;
            connection.setRole(PlayerId.ONE);
        } else if (playerTwo == null) {
            if(Objects.equals(playerOne.getClientName(), connection.getClientName()))
                throw new NameNotAvailableException("Name \"" + playerOne.getClientName() + "\"");

            playerTwo = connection;
            connection.setRole(PlayerId.TWO);
        } else {
            return false;
        }
        return true;
    }

    public boolean removePlayer(WebSocket connection) {
        return removePlayer(getConnection(connection));
    }

    public boolean removePlayer(ClientServerConnection connection) {
        if(connection == null) return false;
        if (Objects.equals(playerOne, connection)) {
            // shift one down
            playerOne = playerTwo;
            playerOne.setRole(PlayerId.ONE);
            playerTwo = null;
        } else if (Objects.equals(playerTwo, connection)) {
            connection.setRole(null);
            playerTwo = null;
        } else {
            return false;
        }
        return true;
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

    public ClientServerConnection getPlayer(PlayerId owner) {
        return owner.ifOne(playerOne, playerTwo);
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
