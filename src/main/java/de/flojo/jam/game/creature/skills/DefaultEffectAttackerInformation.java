package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.BoardCoordinate;

public class DefaultEffectAttackerInformation implements IEffectAttackerInformation{

    private final BoardCoordinate coordinate;
    private final boolean raised;

    public DefaultEffectAttackerInformation(final BoardCoordinate coordinate, final boolean raised) {
        this.coordinate = coordinate;
        this.raised = raised;
    }

    @Override
    public BoardCoordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean isNotRaised() {
        return raised;
    }
}
