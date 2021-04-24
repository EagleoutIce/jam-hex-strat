package de.flojo.jam.util;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.CreatureId;

public class BuildChoice {

    private final boolean isGift;
    private final TerrainId chosenTerrain;
    private final CreatureId chosenCreature;
    private final TrapId chosenTrap;
    private final BoardCoordinate selectedPosition;

    public BuildChoice(TerrainId chosenTerrain, CreatureId chosenCreature, TrapId chosenTrap,
                       BoardCoordinate selectedPosition, boolean isGift) {
        this.chosenTerrain = chosenTerrain;
        this.chosenCreature = chosenCreature;
        this.chosenTrap = chosenTrap;
        this.selectedPosition = selectedPosition;
        this.isGift = isGift;
    }

    public TerrainId getChosenTerrain() {
        return chosenTerrain;
    }

    public CreatureId getChosenCreature() {
        return chosenCreature;
    }

    public TrapId getChosenTrap() {
        return chosenTrap;
    }

    public BoardCoordinate getSelectedPosition() {
        return selectedPosition;
    }

    public boolean isGift() {
        return isGift;
    }
}
