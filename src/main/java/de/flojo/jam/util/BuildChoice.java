package de.flojo.jam.util;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.CreatureId;

public class BuildChoice {

    private TerrainId chosenTerrain;
    private CreatureId chosenCreature;
    private TrapId chosenTrap;
    private BoardCoordinate selectedPosition;
    private boolean isGift;

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

    public void setChosenTerrain(TerrainId chosenTerrain) {
        this.chosenTerrain = chosenTerrain;
    }

    public CreatureId getChosenCreature() {
        return chosenCreature;
    }

    public void setChosenCreature(CreatureId chosenCreature) {
        this.chosenCreature = chosenCreature;
    }

    public TrapId getChosenTrap() {
        return chosenTrap;
    }

    public void setChosenTrap(TrapId chosenTrap) {
        this.chosenTrap = chosenTrap;
    }

    public BoardCoordinate getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(BoardCoordinate selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public boolean isGift() {
        return isGift;
    }
}
