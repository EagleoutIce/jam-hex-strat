package de.flojo.jam.util;

// Hexagon maths :D
public class HexMaths {
    
    private HexMaths() {
        throw new UnsupportedOperationException();
    }

    public static int effectiveWidth(int w) {
        return (int)(Math.ceil(w / 2d));
    }


}
