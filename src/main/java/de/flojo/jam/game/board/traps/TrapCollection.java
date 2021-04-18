package de.flojo.jam.game.board.traps;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrapCollection implements Serializable {

    private static final long serialVersionUID = 8984379100217002426L;

    private static final int DEFAULT_SIZE = 32;

    private final Set<Trap> collection;

    public TrapCollection() {
        collection = Collections.synchronizedSet(new HashSet<>(DEFAULT_SIZE));
    }

    public Optional<Trap> getHighlighted() {
        return search(null, (t, ignored) -> t.isHovered());
    }

    public Optional<Trap> getCollision(final Set<Tile> tiles) {
        return search(null, (t, v) -> t.collidesWith(tiles));
    }

    public Optional<Trap> get(final BoardCoordinate coordinate) {
        return search(coordinate, Trap::coversTile);
    }

    public Optional<Trap> getRoot(final BoardCoordinate coordinate) {
        return search(coordinate, (t, v) -> Objects.equals(t.getCoordinate(), v));
    }

    private <T> Optional<Trap> search(final T val, final BiPredicate<Trap, T> check) {
        synchronized (collection) {
            for (final Trap trap : collection) {
                if (check.test(trap, val)) {
                    return Optional.of(trap);
                }
            }
        }
        return Optional.empty();
    }

    public Trap get(final String name) {
        return search(name, (t, v) -> t.getName().equals(v)).orElse(null);
    }

    public boolean add(final Trap t) {
        synchronized (collection) {
            return collection.add(t);
        }
    }

    public void clear() {
        synchronized (collection) {
            collection.clear();
        }
    }

    public boolean contains(final Trap o) {
        synchronized (collection) {
            return collection.contains(o);
        }
    }

    public boolean isEmpty() {
        synchronized (collection) {
            return collection.isEmpty();
        }
    }

    public int size() {
        synchronized (collection) {
            return collection.size();
        }
    }

    public boolean removeIf(final Predicate<? super Trap> filter) {
        synchronized (collection) {
            return collection.removeIf(filter);
        }
    }

    @Override
    public String toString() {
        return "TrapCollection [collection=" + collection + "]";
    }

    public List<TrapJson> getJsonData() {
        return collection.stream().map(TrapJson::new).collect(Collectors.toList());
    }

}