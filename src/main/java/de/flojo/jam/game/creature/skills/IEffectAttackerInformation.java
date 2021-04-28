package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.creature.CreatureCore;

public interface IEffectAttackerInformation {

    BoardCoordinate getCoordinate();

    boolean isNotRaised();

    default CreatureCore getCore() {
        throw new UnsupportedOperationException();
    }
}
