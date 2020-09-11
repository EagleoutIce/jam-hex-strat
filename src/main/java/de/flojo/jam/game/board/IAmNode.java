package de.flojo.jam.game.board;

public interface IAmNode {
    
    public default IAmNode getChildren() {
        return IAmNode.NONE;
    }

    public static IAmNode NONE = null;
}
