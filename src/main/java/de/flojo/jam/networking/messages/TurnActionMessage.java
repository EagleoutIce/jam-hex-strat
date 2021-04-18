package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.ActionType;
import de.flojo.jam.game.creature.skills.SkillId;

import java.util.List;
import java.util.UUID;

public class TurnActionMessage extends MessageContainer {

    private static final long serialVersionUID = -2797080689160136480L;

    private ActionType action;
    private BoardCoordinate from;
    private List<BoardCoordinate> targets;
    private SkillId skillId;


    public TurnActionMessage(UUID clientId, ActionType action, BoardCoordinate from, List<BoardCoordinate> targets, SkillId skillId) {
        super(MessageTypeEnum.TURN_ACTION, clientId);
        this.action = action;
        this.from = from;
        this.targets = targets;
        this.skillId = skillId;
    }

    public ActionType getAction() {
        return action;
    }

    public BoardCoordinate getFrom() {
        return from;
    }

    public List<BoardCoordinate> getTargets() {
        return targets;
    }

    public BoardCoordinate getTarget() {
        return targets.isEmpty() ? null : targets.get(0);
    }

    public SkillId getSkillId() {
        return skillId;
    }
}