package de.flojo.jam.game.board.traps;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

import de.flojo.jam.game.board.traps.management.TrapData;
import de.flojo.jam.game.board.traps.management.TrapTile;

public enum TrapId {
	T_SPIKE("SpikeTrap", TrapImprint.getSingle(TrapTile.SPIKE), Color.MAGENTA, 250, 4),
	T_BEAR_TRAP("BearTrap", new TrapImprint(new TrapData(//
		Arrays.asList(//
			Arrays.asList(TrapTile.BEAR_TRAP_GHOST, null), //
			Arrays.asList(null, TrapTile.BEAR_TRAP) //
		)), new Point(1, 1)), Color.GREEN, 300, 7);

	private final TrapImprint imprint;
	private final Color simpleColor;
	private final String name;
	private final int animationCooldown;
	private final int cost;

	TrapId(final String name, final TrapImprint imprint, Color simpleColor, int animationCooldown, int cost) {
		this.name = name;
		this.imprint = imprint;
		this.simpleColor = simpleColor;
		this.animationCooldown = animationCooldown;
		this.cost = cost;
	}


	public String getName() {
		return name;
	}

	public TrapImprint getImprint() {
		return imprint;
	}

	public Color getSimpleColor() {
		return simpleColor;
	}

	public int getAnimationCooldown() {
		return animationCooldown;
	}

	public int getCost() {
		return cost;
	}
}
