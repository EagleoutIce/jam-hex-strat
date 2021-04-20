package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.ActionType;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.game.creature.skills.SkillId;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TurnActionMessage extends MessageContainer {

    private static final long serialVersionUID = -2797080689160136480L;

    private final ActionType action;
    private final BoardCoordinate from;
    private final List<BoardCoordinate> targets;
    private final JsonDataOfSkill skillId;

    public TurnActionMessage(UUID clientId, ActionType action, BoardCoordinate from, List<BoardCoordinate> targets, JsonDataOfSkill skillId) {
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

    public JsonDataOfSkill getSkillData() {
        return skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TurnActionMessage that = (TurnActionMessage) o;
        return getAction() == that.getAction() && Objects.equals(getFrom(), that.getFrom()) && Objects.equals(getTargets(), that.getTargets()) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAction(), getFrom(), getTargets(), skillId);
    }
}