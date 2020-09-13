package de.flojo.jam.game.creature;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import de.flojo.jam.game.board.BoardCoordinate;
import de.gurkenlabs.litiengine.graphics.IRenderable;

// removed sort as it will be rendered with joint render and this is more effective
public class CreatureCollection implements IRenderable, Serializable {

    private static final long serialVersionUID = -2951185236311104006L;

    private static final int DEFAULT_SIZE = 32;

    private final transient List<Creature> collection;

    public CreatureCollection() {
        collection = Collections.synchronizedList(new ArrayList<>(DEFAULT_SIZE));
    }

    public Optional<Creature> getHighlighted() {
        return search(null, (c, ignored) -> c.isHovered());
    }

    public Optional<Creature> get(BoardCoordinate coordinate) {
        return search(coordinate, (c, v) -> c.getCoordinate().equals(v));
    }

    private <T> Optional<Creature> search(T val, BiPredicate<Creature, T> check) {
        for (Creature creature : collection) {
            if (check.test(creature, val)) {
                return Optional.of(creature);
            }
        }
        return Optional.empty();
    }

    public Creature get(String name) {
        return search(name, (c, v) -> c.getName().equals(v)).orElse(null);
    }

    @Override
    public void render(Graphics2D g) {
        // keeping render order
        for (Creature creature : collection)
            creature.render(g);
    }

    public boolean add(Creature c) {
        return collection.add(c);
    }

    public void clear() {
        collection.clear();
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public int size() {
        return collection.size();
    }

    public Creature get(int i) {
        return collection.get(i);
    }

    public boolean removeIf(Predicate<? super Creature> filter) {
        return collection.removeIf(filter);
    }

    
    
}
