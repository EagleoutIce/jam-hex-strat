package de.flojo.jam.game.creature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import de.flojo.jam.game.board.BoardCoordinate;

// removed sort as it will be rendered with joint render and this is more effective
public class CreatureCollection implements Serializable {

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
        synchronized(collection) {
            for (Creature creature : collection) {
                if (check.test(creature, val)) {
                    return Optional.of(creature);
                }
            }
        }
        return Optional.empty();
    }

    public Creature get(String name) {
        return search(name, (c, v) -> c.getName().equals(v)).orElse(null);
    }


    public boolean add(Creature c) {
        synchronized(collection) {
            return collection.add(c);
        }
    }

    public void clear() {
        synchronized(collection) {
            collection.clear();
        }
    }

    public boolean contains(Object o) {
        synchronized(collection) {
            return collection.contains(o);
        }
    }

    public boolean isEmpty() {
        synchronized(collection) {
            return collection.isEmpty();
        }
    }

    public int size() {
        synchronized(collection) {
            return collection.size();
        }
    }

    public Creature get(int i) {
        synchronized(collection) {
            return collection.get(i);
        }
    }

    public boolean removeIf(Predicate<? super Creature> filter) {
        synchronized(collection) {
            return collection.removeIf(filter);
        }
    }

    protected boolean remove(Object arg0) {
        synchronized(collection) {
            return collection.remove(arg0);
        }
    }

	public void resetAll() {
        synchronized(collection) {
            for (Creature creature : collection) {
                creature.getAttributes().reset();
            }
        }
    }
}
