package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.player.PlayerId;

@FunctionalInterface
public interface ISummonPlayerCreature {
    Creature summon(String uniqueName, Tile startBase, PlayerId pId, boolean isOur);
}

