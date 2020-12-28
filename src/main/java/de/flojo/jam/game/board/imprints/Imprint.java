package de.flojo.jam.game.board.imprints;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

public abstract class Imprint<T> implements Serializable {

	private static final long serialVersionUID = 6496565213703917547L;

	protected final Point anchor;

	protected final transient BufferedImage bitMap;

	private static final int COLOR_TRUE = Color.WHITE.getRGB();

	public Imprint(final List<List<T>> data, final Point anchor) {
		this.anchor = anchor;
		final int h = data.size();
		final int w = data.isEmpty() ? 0 : data.get(0).size();
		bitMap = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		for (int y = 0; y < h; y++) {
			List<T> tl = data.get(y);
			for (int x = 0; x < w; x++) {
				T type = tl.get(x);
				if(type != null)
					bitMap.setRGB(x, y, COLOR_TRUE);
			}
		}
	}


	public Point getAnchor() {
		return this.anchor;
	}

	public boolean isSet(int x, int y) {
		return bitMap.getRGB(x, y) == COLOR_TRUE;
	}

	public BufferedImage getBitMap() {
		return bitMap;
	}

}

