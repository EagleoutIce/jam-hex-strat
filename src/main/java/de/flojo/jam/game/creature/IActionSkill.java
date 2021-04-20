package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.game.creature.skills.SkillId;

@FunctionalInterface
public interface IActionSkill {
    void onSkill(BoardCoordinate from, BoardCoordinate target, JsonDataOfSkill skill);
}
