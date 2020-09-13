package de.flojo.jam.networking.messages;

import java.util.Objects;

import de.flojo.jam.game.player.PlayerId;

public class HelloMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    private String name;
    private PlayerId role;

    public HelloMessage(String name, PlayerId role) {
        this(name, role, "");
    }

    public HelloMessage(String name, PlayerId role, String debugMessage) {
        super(MessageTypeEnum.HELLO, null, debugMessage);
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return this.name;
    }

    public PlayerId getRole() {
        return this.role;
    }

    @Override
    public String toString() {
        return "HelloMessage [<container>=" + super.toString() + ", name=" + name + ", role=" + role + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, role, super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof HelloMessage))
            return false;
        HelloMessage other = (HelloMessage) obj;
        return Objects.equals(name, other.name) && role == other.role && super.equals(obj);
    }

}