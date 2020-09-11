package de.flojo.jam.game;

public interface IAmNode {
    
    public default IAmNode getChildren() {
        return IAmNode.NONE;
    }

    public static IAmNode NONE = null;
}
