package de.flojo.jam.networking.server;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import org.java_websocket.WebSocket;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.messages.HelloMessage;
import de.flojo.jam.networking.messages.MessageContainer;

public class ClientServerConnection implements Serializable {

	private static final long serialVersionUID = -7696855335934225575L;

	private final UUID clientId;
	private String clientName;
	private PlayerId role;
	private transient WebSocket connection;

	public ClientServerConnection(WebSocket conn, HelloMessage message) {
		this(conn, UUID.randomUUID(), message.getName());
	}

	public ClientServerConnection(WebSocket conn, UUID clientId, String name) {
		this.connection = conn;
		this.clientId = clientId;
		this.clientName = name;

	}

	public UUID getClientId() {
		return this.clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	public WebSocket getConnection() {
		return this.connection;
	}

	public PlayerId getRole() {
		return role;
	}


	public void setRole(PlayerId role) {
		this.role = role;
	}

	public void send(MessageContainer message) {
		message.setClientId(clientId);
		connection.send(message.toJson());
	}


	/**
	 * The probably more readable constant for 'no connection'. <b>DO NOT
	 * CHANGE!</b>
	 */
	public static final ClientServerConnection VOID_CONNECTION = new ClientServerConnection(null, null, null);

	@Override
	public int hashCode() {
		return Objects.hash(clientId, clientName, connection, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ClientServerConnection)) {
			return false;
		}
		ClientServerConnection other = (ClientServerConnection) obj;
		return Objects.equals(clientId, other.clientId) && Objects.equals(clientName, other.clientName)
				&& Objects.equals(connection, other.connection) && role == other.role;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientServerConnection [clientId=").append(clientId).append(", clientName=").append(clientName)
				.append(", connection=").append(connection).append(", role=").append(role).append("]");
		return builder.toString();
	}

}