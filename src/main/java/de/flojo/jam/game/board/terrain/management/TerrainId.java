package de.flojo.jam.game.board.terrain.management;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

import de.flojo.jam.game.board.terrain.TerrainType;


// colors probably useless?
public enum TerrainId {
    T_EMPTY("Leer", TerrainImprint.getSingle(TerrainType.EMPTY), Color.WHITE),
    T_GRASS_HILL("Grashügel", TerrainImprint.getSingle(TerrainType.GRASS_HILL), Color.GREEN),
    T_WDL_LEFT("Wand-L-Links", new TerrainImprint(new TerrainData(//
            Arrays.asList(//
                    Arrays.asList(TerrainType.WDL_LEFT_WINDOW, null), //
                    Arrays.asList(null, TerrainType.WDL_LEFT), //
                    Arrays.asList(null, null), //
                    Arrays.asList(null, TerrainType.WDL_LEFT_SLAVE_1), //
                    Arrays.asList(null, null), //
                    Arrays.asList(null, TerrainType.WDL_LEFT_SLAVE_2))),
            new Point(1, 1)), Color.RED),
    T_WDL_RIGHT("Wand-L-Rechts", new TerrainImprint(new TerrainData(//
            Arrays.asList(//
                    Arrays.asList(null, TerrainType.WDL_RIGHT_WINDOW), //
                    Arrays.asList(TerrainType.WDL_RIGHT, null), //
                    Arrays.asList(null, null), //
                    Arrays.asList(TerrainType.WDL_RIGHT_SLAVE_1, null), //
                    Arrays.asList(null, null), //
                    Arrays.asList(TerrainType.WDL_RIGHT_SLAVE_2, null))),
            new Point(0, 1)), Color.RED.darker()),
    T_DEAD_TREE("Kahler Baum", TerrainImprint.getSingle(TerrainType.DEAD_TREE), Color.YELLOW),
    T_DEAD_TREE_B("Kahler Baum, B", TerrainImprint.getSingle(TerrainType.DEAD_TREE_B), Color.YELLOW.darker()),
    T_CART_LEFT("Karren nach links", new TerrainImprint(new TerrainData(//
            Arrays.asList(//
                    Arrays.asList(null, TerrainType.CART_LEFT_GHOST), //
                    Arrays.asList(TerrainType.CAR_LEFT_HANDLES, null), //
                    Arrays.asList(null, TerrainType.CART_LEFT) //
                    )),new Point(1, 2)),
            Color.ORANGE),
    T_CART_RIGHT("Karren nach rechts", new TerrainImprint(new TerrainData(//
            Arrays.asList(//
                    Arrays.asList(TerrainType.CART_RIGHT_GHOST, null), //
                    Arrays.asList(null, TerrainType.CAR_RIGHT_HANDLES), //
                    Arrays.asList(TerrainType.CART_RIGHT, null) //
                    )),new Point(0, 2)),
            Color.ORANGE.darker());

    private final TerrainImprint imprint;
    private final Color simpleColor;
    private final String name;

    TerrainId(final String name, final TerrainImprint imprint, Color simpleColor) {
        this.name = name;
        this.imprint = imprint;
        this.simpleColor = simpleColor;
    }

    public TerrainImprint getImprint() {
        return imprint;
    }

    public Color getSimpleColor() {
        return simpleColor;
    }

    public String getName() {
        return name;
    }
}
