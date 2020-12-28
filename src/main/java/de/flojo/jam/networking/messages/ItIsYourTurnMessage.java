package de.flojo.jam.networking.messages;

import java.util.UUID;

public class ItIsYourTurnMessage extends MessageContainer {

	private static final long serialVersionUID = -6334082677387248963L;

	public ItIsYourTurnMessage(UUID clientId) {
		this(clientId, "");
	}

	public ItIsYourTurnMessage(UUID clientId, String debugMessage) {
		super(MessageTypeEnum.YOUR_TURN, clientId, debugMessage);
	}

}
