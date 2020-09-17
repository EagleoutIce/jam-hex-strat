package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.skills.SkillId;

public interface IAction {
    
    void onSkip(BoardCoordinate creaturePosition);

    void onMove(BoardCoordinate from, BoardCoordinate target);

    void onSkill(BoardCoordinate from, BoardCoordinate target, SkillId skill);

}
