package de.flojo.jam.util;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.gurkenlabs.litiengine.Game;

public class InputGroup<T extends Serializable> implements IAmInputGroup<T>, Serializable {

	private static final long serialVersionUID = -4505000699173412556L;

	private AtomicBoolean locked;
	private T currentOwner = null;
	private final int lockDuration;


	public InputGroup() {
		this(45);
	}

	public InputGroup(int lockDuration) {
		locked = new AtomicBoolean(false);
		this.lockDuration = lockDuration;
	}

	public boolean tryLock(T owner) {
		synchronized(locked) {
			if(isLocked() && !Objects.equals(owner, currentOwner)) {
				return false;
			}
			this.locked.set(true);
			this.currentOwner = owner;
			Game.loop().perform(lockDuration, this::unlock);
			return true;
		}
	}

	private void unlock() {
		this.locked.set(false);
		this.currentOwner = null;
	}

	public boolean isLocked() {
		return locked.get();
	}

	public T getCurrentOwner() {
		return currentOwner;
	}
}
