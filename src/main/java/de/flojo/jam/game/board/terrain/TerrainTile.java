package de.flojo.jam.game.board.terrain;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.flojo.jam.game.board.PushDirection;
import de.flojo.jam.game.board.terrain.management.TerrainIdConstants;
import de.flojo.jam.game.board.terrain.management.TerrainImprintNodeMap;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.RenderHint;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;
import de.flojo.jam.graphics.renderer.VoidRenderer;

public enum TerrainTile {
    EMPTY("Nothing.", new TerrainImprintNodeMap(TerrainIdConstants.T_EMPTY, 0, 0), false, false, false, false, 1,
	    PushDirection.NONE, VoidRenderer.get()), //
    // dos not blick los or punch as it raised and we include a raised switch
    GRASS_HILL("Ein Grashügel", new TerrainImprintNodeMap(TerrainIdConstants.T_GRASS_HILL, 0, 0), false, false, false, true, 2,
	    PushDirection.NONE, new SimpleImageRenderer("tiles/gelaende_huegel.png", -72 / 2.1d, -73 / 1.33)), //
    //
    WDL_LEFT("Doppel wand mit L-Knick nach links", new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 1), true, true,
	    true, false, -1, PushDirection.NONE,
	    new SimpleImageRenderer("tiles/wand_doppel_l_links.png", -1.65 * 115 / 2d, -242 / 1.84d)), //
    WDL_LEFT_WINDOW("Doppel Wand mit L-Knick nach links:Fenster",
	    new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 0, 0), true, false, true, false, -1, PushDirection.NONE,
	    VoidRenderer.get()),
    WDL_LEFT_SLAVE_1("Doppel Wand mit L-Knick nach links:Geister",
	    new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 3), true, true, true, false, -1, PushDirection.NONE,
	    VoidRenderer.get()),
    WDL_LEFT_SLAVE_2(WDL_LEFT_SLAVE_1, new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_LEFT, 1, 5)),
    //
    WDL_RIGHT("Doppel wand mit L-Knick nach rechts", new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 1), true,
	    true, true, false, -1, PushDirection.NONE,
	    new SimpleImageRenderer("tiles/wand_doppel_l_rechts.png", -115 / 6d, -242 / 1.84d)), //
    WDL_RIGHT_WINDOW("Doppel Wand mit L-Knick nach rechts:Fenster",
	    new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 1, 0), true, false, true, false, -1, PushDirection.NONE,
	    VoidRenderer.get()),
    WDL_RIGHT_SLAVE_1("Doppel Wand mit L-Knick nach rechts:Geister",
	    new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 3), true, true, true, false, -1, PushDirection.NONE,
	    VoidRenderer.get()),
    WDL_RIGHT_SLAVE_2(WDL_RIGHT_SLAVE_1, new TerrainImprintNodeMap(TerrainIdConstants.T_WDL_RIGHT, 0, 5)),
    //
    DEAD_TREE("Toter Baumstumpf", new TerrainImprintNodeMap(TerrainIdConstants.T_DEAD_TREE, 0, 0), true, true, true, false,
	    -1, PushDirection.NONE, new SimpleImageRenderer("tiles/baum_kahl.png", -89 / 2d, -149 / 1.25d)), //
    //
    DEAD_TREE_B("Toter Baumstumpf, Variante B", new TerrainImprintNodeMap(TerrainIdConstants.T_DEAD_TREE_B, 0, 0), true, true, true, false,
    -1, PushDirection.NONE, new SimpleImageRenderer("tiles/baum_kahl_variante_b.png", -89 / 2d, -149 / 1.55d)), //
    //
    CART_LEFT("Kaputter Wagen nach links", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_LEFT, 1, 2), true, true, true,
	    false, -1, PushDirection.NONE, new SimpleImageRenderer("tiles/karren_links.png", -142 / 1.25d, -75 / 1.1d)), //
    CART_LEFT_GHOST("Kaputter Wagen nach links:Geister", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_LEFT, 1, 0), true,
	    true, true, false, -1, PushDirection.NONE, VoidRenderer.get()),
    CAR_LEFT_HANDLES("Kaputter Wagen nach links:Griffe", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_LEFT, 0, 1), true,
	    false, false, false, -1, PushDirection.NONE, VoidRenderer.get()),
    //
    CART_RIGHT("Kaputter Wagen nach rechts", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_RIGHT, 0, 2), true, true,
    true, false, -1, PushDirection.NONE,
	    new SimpleImageRenderer("tiles/karren_rechts.png", -142 / 4.85d, -75 / 1.1d)), //
    CART_RIGHT_GHOST("Kaputter Wagen nach rechts:Geister", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_RIGHT, 0, 0),
	    true, true, true, false, -1, PushDirection.NONE, VoidRenderer.get()),
    CAR_RIGHT_HANDLES("Kaputter Wagen nach rechts:Griffe", new TerrainImprintNodeMap(TerrainIdConstants.T_CART_RIGHT, 1, 1),
	    true, false, false, false, -1, PushDirection.NONE, VoidRenderer.get()),;

    private final String displayName;
    private final boolean blocksWalking;
    private final boolean blocksLineOfSight;
    private final boolean blocksOnPunch;
    private final boolean raised;
    private final int movementCost;
    private final PushDirection pushDirection;
    private final IRenderData renderer;
    private final TerrainImprintNodeMap node;

    @SuppressWarnings("java:S107")
    private TerrainTile(String displayName, TerrainImprintNodeMap node, boolean blocksWalking, boolean blocksLineOfSight,
	    boolean blocksOnPunch, boolean raised, int movementCost, PushDirection pushDirection, final IRenderData renderer) {
	this.displayName = displayName;
	this.blocksWalking = blocksWalking;
	this.blocksLineOfSight = blocksLineOfSight;
	this.blocksOnPunch = blocksOnPunch;
	this.raised = raised;
	this.movementCost = movementCost;
	this.pushDirection = pushDirection;
	this.renderer = renderer;
	this.node = node;
    }

    private TerrainTile(TerrainTile copy, TerrainImprintNodeMap node) {
	this(copy.displayName, node, copy.blocksWalking, copy.blocksLineOfSight, copy.blocksOnPunch, copy.raised,
		copy.movementCost, copy.pushDirection, copy.renderer);
    }

    public String getDisplayName() {
	return displayName;
    }

    public boolean blocksLineOfSight() {
	return blocksLineOfSight;
    }

    public boolean blocksPunching() {
	return blocksOnPunch;
    }

    public PushDirection getPushDirection() {
	return pushDirection;
    }

    public IRenderData getRenderer() {
	return renderer;
    }

    public boolean blocksWalking() {
	return blocksWalking;
    }

    public boolean isRaised() {
	return raised;
    }

    public int getCost() {
	return movementCost;
    }

    public void render(Graphics2D g, Point2D pos, RenderHint hint) {
	renderer.render(g, pos, hint);
    }

    public TerrainImprintNodeMap getNode() {
	return node;
    }

}
