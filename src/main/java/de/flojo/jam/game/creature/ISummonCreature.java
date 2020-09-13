package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;

@FunctionalInterface
public interface ISummonCreature {
    
    Creature summon(String uniqueName, Tile startBase);

}

