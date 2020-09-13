package de.flojo.jam.networking.messages;

public enum MessageTypeEnum {
    HELLO(HelloMessage.class),
    HELLO_REPLY(HelloReplyMessage.class),
    ERROR(ErrorMessage.class),
    GAME_START(GameStartMessage.class);
    private final Class<? extends MessageContainer> targetClass;

    MessageTypeEnum(Class<? extends MessageContainer> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<? extends MessageContainer> getTargetClass() {
        return targetClass;
    }

}