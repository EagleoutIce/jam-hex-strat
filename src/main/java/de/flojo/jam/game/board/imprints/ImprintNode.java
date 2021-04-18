package de.flojo.jam.game.board.imprints;

import java.awt.*;

public interface ImprintNode<T> {
    Imprint<T> getImprint();

    Point getPos();
}