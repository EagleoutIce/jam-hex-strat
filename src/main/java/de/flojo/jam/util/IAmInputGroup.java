package de.flojo.jam.util;

public interface IAmInputGroup<T>{
    
    public boolean tryLock(T owner);

    boolean isLocked();
    public T getCurrentOwner();

}
