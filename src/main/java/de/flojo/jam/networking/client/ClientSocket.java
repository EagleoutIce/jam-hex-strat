package de.flojo.jam.networking.client;

import java.net.URI;
import java.util.logging.Level;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import de.gurkenlabs.litiengine.Game;

public class ClientSocket extends WebSocketClient {

	private IClientController controller;

	public ClientSocket(URI serverUri, IClientController controller) {
		super(serverUri);
		this.controller = controller;
		Game.log().log(Level.INFO, "Starting client on uri: {0}", serverUri );
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
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
		Game.log().severe(ex.getMessage());
	}

}