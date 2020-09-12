package de.flojo.jam.game.creature;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import de.flojo.jam.game.board.BoardCoordinate;
import de.gurkenlabs.litiengine.graphics.IRenderable;

public class CreatureCollection extends LinkedList<Creature> implements IRenderable {

    private static final long serialVersionUID = -2951185236311104006L;

    public CreatureCollection(Collection<? extends Creature> c) {
        super(c);
    }

    public CreatureCollection() {
    }

    public Optional<Creature> findAt(BoardCoordinate coordinate) {
        for (Creature creature : this) {
            if (creature.getCoordinate().equals(coordinate)) {
                return Optional.of(creature);
            }
        }
        return Optional.empty();
    }

    @Override
    public void render(Graphics2D g) {
        this.forEach(c -> c.render(g));
    }

    
    
}
