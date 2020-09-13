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

public class CreatureCollection implements IRenderable, Serializable {

    private static final long serialVersionUID = -2951185236311104006L;

    private static int DEFAULT_SIZE = 32;

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

    public void sort() {
        this.collection.sort((c1, c2) -> {
            if (c1.getCoordinate().y == c2.getCoordinate().y)
                return Integer.compare(c1.getCoordinate().x, c2.getCoordinate().x);
            else
                return Integer.compare(c1.getCoordinate().y, c2.getCoordinate().y);
        });
    }

    public boolean add(Creature c) {
        boolean feedback = collection.add(c);
        sort();
        return feedback;
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
        boolean feedback = collection.removeIf(filter);
        sort();
        return feedback;
    }

    
    
}
