package de.flojo.jam.networking.messages;

import java.util.UUID;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.ActionType;
import de.flojo.jam.game.creature.skills.SkillId;

public class TurnActionMessage extends MessageContainer {

    private static final long serialVersionUID = -2797080689160136480L;

    private ActionType action;
    private BoardCoordinate from;
    private BoardCoordinate target;
    private SkillId skillId;

    public TurnActionMessage(UUID clientId, ActionType action, BoardCoordinate from, BoardCoordinate target, SkillId skillId) {
        super(MessageTypeEnum.TURN_ACTION, clientId);
        this.action = action;
        this.from = from;
        this.target = target;
        this.skillId = skillId;
    }

    public ActionType getAction() {
        return action;
    }

    public BoardCoordinate getFrom() {
        return from;
    }

    public BoardCoordinate getTarget() {
        return target;
    }

    public SkillId getSkillId() {
        return skillId;
    }
}