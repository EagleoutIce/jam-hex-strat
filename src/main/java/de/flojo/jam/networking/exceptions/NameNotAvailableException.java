package de.flojo.jam.networking.exceptions;

public class NameNotAvailableException extends HandlerException {

	private static final long serialVersionUID = 5692162166830810314L;

	public NameNotAvailableException(String message) {
		super(message, ErrorTypeEnum.NAME_NOT_AVAILABLE);
	}
}