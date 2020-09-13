package de.flojo.jam.networking.messages;

public class StartGameMessage extends MessageContainer {

    private static final long serialVersionUID = 7630983891460330082L;

    public StartGameMessage() {
        super(MessageTypeEnum.START_GAME, null, "");
    }

    public StartGameMessage(String debugMessage) {
        super(MessageTypeEnum.START_GAME, null, debugMessage);
    }

    @Override
    public String toString() {
        return "StartGame [<container>=" + super.toString() + "]";
    }


}