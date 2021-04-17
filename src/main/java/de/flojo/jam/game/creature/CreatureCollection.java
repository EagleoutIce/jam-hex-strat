package de.flojo.jam.game.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.player.PlayerId;

// removed sort as it will be rendered with joint render and this is more effective
public class CreatureCollection {

	private static final int DEFAULT_SIZE = 32;

	private final List<Creature> collection;

	public CreatureCollection() {
		collection = Collections.synchronizedList(new ArrayList<>(DEFAULT_SIZE));
	}

	public Optional<Creature> getHighlighted() {
		return search(null, (c, ignored) -> c.isHovered());
	}

	public Optional<Creature> get(BoardCoordinate coordinate) {
		return search(coordinate, (c, v) -> c.getCoordinate().equals(v));
	}

	public Optional<Creature> get(Set<Tile> tiles) {
		for (Tile tile : tiles) {
			if(tile == null)
				continue;
			Optional<Creature> mayCreature = get(tile.getCoordinate());
			if(mayCreature.isPresent())
				return mayCreature;
		}
		return Optional.empty();
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

	public boolean playerOneOwns() {
		return collection.stream().anyMatch(c -> !c.isMoribund() && c.getOwner() == PlayerId.ONE);
	}

	public boolean playerTwoOwns() {
		return collection.stream().anyMatch(c -> !c.isMoribund() && c.getOwner() == PlayerId.TWO);
	}

	public boolean noneCanDoSomething() {
		return collection.stream().noneMatch(Creature::canDoSomething);
	}

	public void addAll(CreatureCollection creatures) {
		this.collection.addAll(creatures.collection);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreatureCollection [collection=").append(collection).append("]");
		return builder.toString();
	}

	public List<CreatureJson> getJsonData() {
		return collection.stream().map(CreatureJson::new).collect(Collectors.toList());
	}

	public boolean p1CanDoSomething() {
		return collection.stream().filter(c -> c.getOwner() == PlayerId.ONE).anyMatch(Creature::canDoSomething);
	}

	public boolean p2CanDoSomething() {
		return collection.stream().filter(c -> c.getOwner() == PlayerId.TWO).anyMatch(Creature::canDoSomething);
	}


}
