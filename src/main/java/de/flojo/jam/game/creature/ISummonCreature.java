package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;


public interface ISummonCreature {
    
    Creature summon(String uniqueName, Tile startBase);
}

