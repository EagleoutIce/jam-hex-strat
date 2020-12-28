package de.flojo.jam.networking.messages;

import java.util.Objects;

public class HelloMessage extends MessageContainer {

	private static final long serialVersionUID = 7630983891460330082L;

	private String name;

	public HelloMessage(String name) {
		this(name, "");
	}

	public HelloMessage(String name, String debugMessage) {
		super(MessageTypeEnum.HELLO, null, debugMessage);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "HelloMessage [<container>=" + super.toString() + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof HelloMessage))
			return false;
		HelloMessage other = (HelloMessage) obj;
		return Objects.equals(name, other.name) && super.equals(obj);
	}

}