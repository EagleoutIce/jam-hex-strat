package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.creature.CreatureCollection;

public class DefaultEffectContext implements IProvideEffectContext {
    private final Board board;
    private final CreatureCollection collection;

    public DefaultEffectContext(Board board, CreatureCollection collection) {
        this.board = board;
        this.collection = collection;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public CreatureCollection getCreatures() {
        return collection;
    }
}
