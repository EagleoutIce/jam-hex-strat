package de.flojo.jam.game.board.terrain.management;

import de.flojo.jam.game.board.terrain.TerrainTile;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;


// colors probably useless?
public enum TerrainId {
    T_EMPTY("Leer", TerrainImprint.getSingle(TerrainTile.EMPTY), Color.WHITE, 0),
    T_GRASS_HILL("Grashügel", TerrainImprint.getSingle(TerrainTile.GRASS_HILL), Color.GREEN, 2),
    T_BELT("Belt", TerrainImprint.getSingle(TerrainTile.BELT), Color.GREEN, 5, true),
    T_WDL_LEFT("Wand-L-Links", new TerrainImprint(new TerrainData(//
                                                                  Arrays.asList(//
                                                                                Arrays.asList(
                                                                                        TerrainTile.WDL_LEFT_WINDOW,
                                                                                        null), //
                                                                                Arrays.asList(null,
                                                                                              TerrainTile.WDL_LEFT), //
                                                                                Arrays.asList(null, null), //
                                                                                Arrays.asList(null,
                                                                                              TerrainTile.WDL_LEFT_SLAVE_1),
                                                                                //
                                                                                Arrays.asList(null, null), //
                                                                                Arrays.asList(null,
                                                                                              TerrainTile.WDL_LEFT_SLAVE_2))),
                                                  new Point(1, 1)), Color.RED, 7),
    T_WDL_RIGHT("Wand-L-Rechts", new TerrainImprint(new TerrainData(//
                                                                    Arrays.asList(//
                                                                                  Arrays.asList(null,
                                                                                                TerrainTile.WDL_RIGHT_WINDOW),
                                                                                  //
                                                                                  Arrays.asList(TerrainTile.WDL_RIGHT,
                                                                                                null), //
                                                                                  Arrays.asList(null, null), //
                                                                                  Arrays.asList(
                                                                                          TerrainTile.WDL_RIGHT_SLAVE_1,
                                                                                          null), //
                                                                                  Arrays.asList(null, null), //
                                                                                  Arrays.asList(
                                                                                          TerrainTile.WDL_RIGHT_SLAVE_2,
                                                                                          null))),
                                                    new Point(0, 1)), Color.RED.darker(), 7),
    T_DEAD_TREE("Baum kahl", TerrainImprint.getSingle(TerrainTile.DEAD_TREE), Color.YELLOW, 1),
    T_DEAD_TREE_B("Baum kahl, B", TerrainImprint.getSingle(TerrainTile.DEAD_TREE_B), Color.YELLOW.darker(), 1),
    T_CART_LEFT("Karren links", new TerrainImprint(new TerrainData(//
                                                                   Arrays.asList(//
                                                                                 Arrays.asList(null,
                                                                                               TerrainTile.CART_LEFT_GHOST),
                                                                                 //
                                                                                 Arrays.asList(
                                                                                         TerrainTile.CAR_LEFT_HANDLES,
                                                                                         null), //
                                                                                 Arrays.asList(null,
                                                                                               TerrainTile.CART_LEFT) //
                                                                   )), new Point(1, 2)),
                Color.ORANGE, 4),
    T_CART_RIGHT("Karren rechts", new TerrainImprint(new TerrainData(//
                                                                     Arrays.asList(//
                                                                                   Arrays.asList(
                                                                                           TerrainTile.CART_RIGHT_GHOST,
                                                                                           null), //
                                                                                   Arrays.asList(null,
                                                                                                 TerrainTile.CAR_RIGHT_HANDLES),
                                                                                   //
                                                                                   Arrays.asList(TerrainTile.CART_RIGHT,
                                                                                                 null) //
                                                                     )), new Point(0, 2)),
                 Color.ORANGE.darker(), 4);

    private final TerrainImprint imprint;
    private final Color simpleColor;
    private final String name;
    private final int cost;
    // TODO: rotation modes
    private final boolean rotatable;

    TerrainId(final String name, final TerrainImprint imprint, Color simpleColor, final int cost) {
        this(name, imprint, simpleColor, cost, false);
    }

    TerrainId(
            final String name, final TerrainImprint imprint, Color simpleColor, final int cost,
            final boolean rotatable) {
        this.name = name;
        this.imprint = imprint;
        this.simpleColor = simpleColor;
        this.cost = cost;
        this.rotatable = rotatable;
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

    public int getCost() {
        return cost;
    }

    public boolean isRotatable() {
        return rotatable;
    }
}
