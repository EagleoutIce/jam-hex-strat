package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.creature.CreatureCollection;

public interface IProvideEffectContext {

    Board getBoard();
    CreatureCollection getCreatures();
    
}
