package de.flojo.jam.game.board.traps;

import java.awt.Point;
import java.awt.image.BufferedImage;

import de.flojo.jam.game.board.imprints.Imprint;

public class TrapImprint extends Imprint<TrapTile> {

    private static final long serialVersionUID = -7701536199670624780L;

    private final TrapData data;
    private final TrapTile baseTile;


    public TrapImprint(TrapData data, final Point anchor) {
        super(data, anchor);
        this.data = data;
        this.baseTile = this.data.getTrapTileAt(anchor.x, anchor.y).orElseThrow();
    }

    public TrapData getData() {
        return data;
    }

    public BufferedImage getNormalImage() {
        return baseTile.getNormalRenderer().getImage();
    }

    public BufferedImage getTriggeredImage() {
        return baseTile.getTriggeredRenderer().getImage();
    }


    public static final TrapImprint getSingle(TrapTile type) { return new TrapImprint(new TrapData(type), new Point(0,0)); }

}
