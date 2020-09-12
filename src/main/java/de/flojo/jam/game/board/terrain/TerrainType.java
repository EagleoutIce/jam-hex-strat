package de.flojo.jam.game.board.terrain;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.flojo.jam.game.board.Direction;
import de.flojo.jam.game.board.terrain.management.ImprintNode;
import de.flojo.jam.game.board.terrain.management.ImprintNodeMap;
import de.flojo.jam.game.board.terrain.management.TerrainIdConstants;
import de.flojo.jam.game.board.terrain.renderer.IRenderData;
import de.flojo.jam.game.board.terrain.renderer.SimpleImageRenderer;
import de.flojo.jam.game.board.terrain.renderer.VoidRenderer;

public enum TerrainType {
    EMPTY("Nothing.", new ImprintNodeMap(TerrainIdConstants.T_EMPTY, 0, 0), false, false, false, false, 1,
            Direction.NONE, VoidRenderer.get()), //
    //
    GRASS_HILL("Ein Grashügel", new ImprintNodeMap(TerrainIdConstants.T_GRASS_HILL, 0, 0), false, true, false, true,
            2, Direction.NONE, new SimpleImageRenderer("tiles/gelaende_huegel.png", -72 / 2d, -79 / 1.33)), //
    //
    WDL_LEFT("Doppel wand mit L-Knick nach links", new ImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 1), true,
            true, false, false, -1, Direction.NONE,
            new SimpleImageRenderer("tiles/wand_doppel_l_links.png", -1.65 * 115 / 2d, -242 / 1.84d)), //
    WDL_LEFT_WINDOW("Doppel Wand mit L-Knick nach links:Fenster",
            new ImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 0, 0), true, false, false, false, -1, Direction.NONE,
            VoidRenderer.get()),
    WDL_LEFT_SLAVE_1("Doppel Wand mit L-Knick nach links:Geister",
            new ImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 3), true, true, false, false, -1, Direction.NONE,
            VoidRenderer.get()),
    WDL_LEFT_SLAVE_2(WDL_LEFT_SLAVE_1, new ImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 5)),
    //
    WDL_RIGHT("Doppel wand mit L-Knick nach rechts", new ImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 1), true,
            true, false, false, -1, Direction.NONE,
            new SimpleImageRenderer("tiles/wand_doppel_l_rechts.png", -115 / 6d, -242 / 1.84d)), //
    WDL_RIGHT_WINDOW("Doppel Wand mit L-Knick nach rechts:Fenster",
            new ImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 1, 0), true, false, false, false, -1, Direction.NONE,
            VoidRenderer.get()),
    WDL_RIGHT_SLAVE_1("Doppel Wand mit L-Knick nach rechts:Geister",
            new ImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 3), true, true, false, false, -1, Direction.NONE,
            VoidRenderer.get()),
    WDL_RIGHT_SLAVE_2(WDL_RIGHT_SLAVE_1, new ImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 5)),
    //
    DEAD_TREE("Toter Baumstumpf", new ImprintNodeMap(TerrainIdConstants.T_DEAD_TREE, 0, 0), true,
        true, false, false, -1, Direction.NONE,
        new SimpleImageRenderer("tiles/baum_kahl.png", - 89 / 2d, -149 / 1.25d)), //
    ;

    private final String displayName;
    private final boolean blocksWalking;
    private final boolean blocksLineOfSight;
    private final boolean blocksFromSky;
    private final boolean raised;
    private final int cost;
    private final Direction pushDirection;
    private final IRenderData renderer;
    private final ImprintNode node;

    // TODO: pattern if multiple; offset data etc.
    @SuppressWarnings("java:S107")
    private TerrainType(String displayName, ImprintNode node, boolean blocksWalking, boolean blocksLineOfSight,
            boolean blocksFromSky, boolean raised, int cost, Direction pushDirection, final IRenderData renderer) {
        this.displayName = displayName;
        this.blocksWalking = blocksWalking;
        this.blocksLineOfSight = blocksLineOfSight;
        this.blocksFromSky = blocksFromSky;
        this.raised = raised;
        this.cost = cost;
        this.pushDirection = pushDirection;
        this.renderer = renderer;
        this.node = node;
    }

    private TerrainType(TerrainType copy, ImprintNode node) {
        this(copy.displayName, node, copy.blocksWalking, copy.blocksLineOfSight, copy.blocksFromSky, copy.raised,
                copy.cost, copy.pushDirection, copy.renderer);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBlocksLineOfSight() {
        return blocksLineOfSight;
    }

    public boolean isBlocksFromSky() {
        return blocksFromSky;
    }

    public Direction getPushDirection() {
        return pushDirection;
    }

    public IRenderData getRenderer() {
        return renderer;
    }

    public boolean isBlocksWalking() {
        return blocksWalking;
    }

    public boolean isRaised() {
        return raised;
    }

    public int getCost() {
        return cost;
    }

    public void render(Graphics2D g, Point2D pos, boolean highlight) {
        renderer.render(g, pos, highlight);
    }

    public ImprintNode getNode() {
        return node;
    }

}
