package de.flojo.jam.game.board.traps;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;

public class TrapCollection implements Serializable {

    private static final long serialVersionUID = -8494961631864802815L;

    private static final int DEFAULT_SIZE = 32;

    private final transient Set<Trap> collection;

    public TrapCollection() {
        collection = Collections.synchronizedSet(new HashSet<>(DEFAULT_SIZE));
    }

    public Optional<Trap> getHighlighted() {
        return search(null, (t, ignored) -> t.isHovered());
    }

    public Optional<Trap> getCollision(Set<Tile> tiles) {
        return search(null, (t, v) -> t.collidesWith(tiles));
    }

    public Optional<Trap> get(BoardCoordinate coordinate) {
        return search(coordinate, (t, v) -> t.coversTile(v));
    }

    public Optional<Trap> getRoot(BoardCoordinate coordinate) {
        return search(coordinate, (t, v) -> Objects.equals(t.getCoordinate(),v));
    }

    private <T> Optional<Trap> search(T val, BiPredicate<Trap, T> check) {
        synchronized(collection) {
            for (Trap trap : collection) {
                if (check.test(trap, val)) {
                    return Optional.of(trap);
                }
            }
        }
        return Optional.empty();
    }

    public Trap get(String name) {
        return search(name, (t, v) -> t.getName().equals(v)).orElse(null);
    }

    public boolean add(Trap t) {
        synchronized(collection) {
            return collection.add(t);
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

    public boolean removeIf(Predicate<? super Trap> filter) {
        synchronized(collection) {
            return collection.removeIf(filter);
        }
    }
    
}