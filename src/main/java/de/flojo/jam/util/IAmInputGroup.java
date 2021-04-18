package de.flojo.jam.util;

public interface IAmInputGroup<T> {
    boolean tryLock(T owner);

    boolean isLocked();

    T getCurrentOwner();
}
