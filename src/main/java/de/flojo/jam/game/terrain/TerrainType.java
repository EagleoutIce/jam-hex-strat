package de.flojo.jam.game.terrain;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.flojo.jam.game.Direction;
import de.flojo.jam.game.terrain.renderer.IRenderData;
import de.flojo.jam.game.terrain.renderer.SimpleImageRenderer;
import de.flojo.jam.game.terrain.renderer.VoidRenderer;

public enum TerrainType {
    EMPTY(TerrainId.T_EMPTY, "Nothing.", false, false, false, false, 1, Direction.NONE, VoidRenderer.get()), //
    // 
    GRASS_HILL(TerrainId.T_GRASS_HILL, "Ein Grashügel", false, true, false, true, 2, Direction.NONE,
            new SimpleImageRenderer("tiles/gelaende_huegel.png", -72 / 2d, -79 / 1.33)), //
    //
    WDL_LEFT(TerrainId.T_WDL_LEFT, "Doppel wand mit L-Knick nach links", true, true, false, false, -1, Direction.NONE,
            new SimpleImageRenderer("tiles/wand_doppel_l_links.png", -1.65 * 115 / 2d, -242 / 1.84d)), //
    WDL_LEFT_WINDOW(TerrainId.T_WDL_LEFT, "Doppel Wand mit L-Knick nach links:Fenster", true, false, false, false, -1,
            Direction.NONE, VoidRenderer.get()),
    WDL_LEFT_SLAVE(TerrainId.T_WDL_LEFT, "Doppel Wand mit L-Knick nach links:Geister", true, true, false, false, -1,
            Direction.NONE, VoidRenderer.get()),
    //
    WDL_RIGHT(TerrainId.T_WDL_RIGHT, "Doppel wand mit L-Knick nach right", true, true, false, false, -1, Direction.NONE,
            new SimpleImageRenderer("tiles/wand_doppel_l_rechts.png", -115 / 6d, -242 / 1.84d)), //
    WDL_RIGHT_WINDOW(TerrainId.T_WDL_RIGHT, "Doppel Wand mit L-Knick nach rechts:Fenster", true, false, false, false, -1,
            Direction.NONE, VoidRenderer.get()),
    WDL_RIGHT_SLAVE(TerrainId.T_WDL_RIGHT, "Doppel Wand mit L-Knick nach rechts:Geister", true, true, false, false, -1,
            Direction.NONE, VoidRenderer.get());

    private final String displayName;
    private final TerrainId tileId;
    private final boolean blocksWalking;
    private final boolean blocksLineOfSight;
    private final boolean blocksFromSky;
    private final boolean raised;
    private final int cost;
    private final Direction pushDirection;
    private final IRenderData renderer;

    // TODO: pattern if multiple; offset data etc.
    @SuppressWarnings("java:S107")
    private TerrainType(final TerrainId tileId, String displayName, boolean blocksWalking, boolean blocksLineOfSight,
            boolean blocksFromSky, boolean raised, int cost, Direction pushDirection, final IRenderData renderer) {
        this.tileId = tileId;
        this.displayName = displayName;
        this.blocksWalking = blocksWalking;
        this.blocksLineOfSight = blocksLineOfSight;
        this.blocksFromSky = blocksFromSky;
        this.raised = raised;
        this.cost = cost;
        this.pushDirection = pushDirection;
        this.renderer = renderer;
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
    
    public TerrainId getTerrainId() {
        return tileId;
    }
}
