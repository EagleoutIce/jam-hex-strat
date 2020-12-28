package de.flojo.jam.game.board.terrain.management;

import java.awt.Point;
import java.awt.image.BufferedImage;

import de.flojo.jam.game.board.imprints.Imprint;
import de.flojo.jam.game.board.terrain.TerrainTile;

public class TerrainImprint extends Imprint<TerrainTile> {

	private static final long serialVersionUID = -7701536199670624780L;

	private final TerrainData data;
	private final TerrainTile baseType;


	public TerrainImprint(TerrainData data, final Point anchor) {
		super(data, anchor);
		this.data = data;
		this.baseType = this.data.getTerrainAt(anchor.x, anchor.y);
	}

	public TerrainData getData() {
		return data;
	}

	public boolean hasBaseResource() {
		return baseType.getRenderer().hasImage();
	}

	public BufferedImage getBaseResource() {
		return baseType.getRenderer().getImage();
	}

	public static final TerrainImprint getSingle(TerrainTile type) { return new TerrainImprint(new TerrainData(type), new Point(0,0)); }

}
