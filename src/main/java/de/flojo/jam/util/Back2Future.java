package de.flojo.jam.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Back2Future<T> implements Future<T> {
    private final T value;
    public Back2Future(T value) {
        this.value = value;
    }

    @Override
    public boolean cancel(boolean b) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return value;
    }

    @Override
    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return value;
    }
}
