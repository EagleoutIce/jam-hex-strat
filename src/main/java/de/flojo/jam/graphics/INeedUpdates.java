package de.flojo.jam.graphics;

@FunctionalInterface
public interface INeedUpdates<T> {
    void call(T[] data);
}
