package de.flojo.jam.networking.messages;

public enum MessageTypeEnum {
    HELLO(HelloMessage.class),
    HELLO_REPLY(HelloReplyMessage.class),
    ERROR(ErrorMessage.class),
    BUILD_START(BuildPhaseStartMessage.class),
    YOU_CAN_BUILD(YouCanBuildMessage.class),
    BUILD_CHOICE(BuildChoiceMessage.class),
    BUILD_UPDATE(BuildUpdateMessage.class),
    GAME_START(GameStartMessage.class),
    NEXT_ROUND(NextRoundMessage.class),
    YOUR_TURN(ItIsYourTurnMessage.class),
    TURN_ACTION(TurnActionMessage.class),
    GAME_OVER(GameOverMessage.class);

    private final Class<? extends MessageContainer> targetClass;

    MessageTypeEnum(Class<? extends MessageContainer> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<? extends MessageContainer> getTargetClass() {
        return targetClass;
    }
}
