package de.flojo.jam.networking.exceptions;

public class NoConnectionException extends GenericNetworkingException {

	private static final long serialVersionUID = -8528028291448851677L;

	public NoConnectionException(int seconds) {
		super("Wasn't able to establish a connection with the server. Waited:" + seconds + "s.");
	}
}