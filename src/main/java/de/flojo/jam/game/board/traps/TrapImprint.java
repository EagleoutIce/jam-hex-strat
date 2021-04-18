package de.flojo.jam.game.board.traps;

import de.flojo.jam.game.board.imprints.Imprint;
import de.flojo.jam.game.board.traps.management.TrapData;
import de.flojo.jam.game.board.traps.management.TrapTile;
import de.flojo.jam.graphics.renderer.IRenderData;

import java.awt.*;

public class TrapImprint extends Imprint<TrapTile> {

    private static final long serialVersionUID = -7701536199670624780L;

    private final TrapData data;
    private final TrapTile baseTile;

    public TrapImprint(TrapData data, final Point anchor) {
        super(data, anchor);
        this.data = data;
        this.baseTile = this.data.getTrapTileAt(anchor.x, anchor.y).orElseThrow();
    }

    public static final TrapImprint getSingle(TrapTile type) {
        return new TrapImprint(new TrapData(type), new Point(0, 0));
    }

    public TrapData getData() {
        return data;
    }

    public IRenderData getNormalRenderer() {
        return baseTile.getNormalRenderer();
    }

    public IRenderData getTriggeredRenderer() {
        return baseTile.getTriggeredRenderer();
    }

}
