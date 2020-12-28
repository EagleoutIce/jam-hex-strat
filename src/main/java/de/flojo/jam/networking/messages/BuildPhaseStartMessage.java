package de.flojo.jam.networking.messages;

import java.util.Objects;
import java.util.UUID;

import de.flojo.jam.networking.server.PlayerController;

public class BuildPhaseStartMessage extends MessageContainer {

	private static final long serialVersionUID = 7630983891460330082L;

	private UUID p1Id;
	private UUID p2Id;

	private String p1Name;
	private String p2Name;

	public BuildPhaseStartMessage(UUID clientId, PlayerController c) {
		this(clientId, c.getPlayerOne().getClientId(), c.getPlayerTwo().getClientId(), c.getPlayerOne().getClientName(), c.getPlayerTwo().getClientName(), "");
	}


	public BuildPhaseStartMessage(UUID clientId, UUID p1Id, UUID p2Id, String p1Name, String p2Name) {
		this(clientId, p1Id, p2Id, p1Name, p2Name, "");
	}

	public BuildPhaseStartMessage(UUID clientId, UUID p1Id, UUID p2Id, String p1Name, String p2Name, String debugMessage) {
		super(MessageTypeEnum.BUILD_START, clientId, debugMessage);
		this.p1Id = p1Id;
		this.p2Id = p2Id;
		this.p1Name = p1Name;
		this.p2Name = p2Name;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(p1Id, p1Name, p2Id, p2Name);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof BuildPhaseStartMessage)) {
			return false;
		}
		BuildPhaseStartMessage other = (BuildPhaseStartMessage) obj;
		return Objects.equals(p1Id, other.p1Id) && Objects.equals(p1Name, other.p1Name)
				&& Objects.equals(p2Id, other.p2Id) && Objects.equals(p2Name, other.p2Name);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameStartMessage [<container>=").append(super.toString()).append(", p1Id=").append(p1Id).append(", p1Name=").append(p1Name).append(", p2Id=")
				.append(p2Id).append(", p2Name=").append(p2Name).append("]");
		return builder.toString();
	}


	public UUID getP1Id() {
		return p1Id;
	}

	public UUID getP2Id() {
		return p2Id;
	}

	public String getP1Name() {
		return p1Name;
	}

	public String getP2Name() {
		return p2Name;
	}


}