package de.flojo.jam.game.board.traps.management;

import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;
import de.flojo.jam.graphics.renderer.VoidRenderer;

public enum TrapTile {
    SPIKE("Stachelfalle", new SimpleImageRenderer("tiles/traps/stachelfalle.png", -69 / 2.15d, -51 / 1.55),
            new SimpleImageRenderer("tiles/traps/stachelfalle_aktiv.png", -69 / 2.15d, -51 / 1.55),
            new TrapImprintNodeMap(TrapIdConstants.T_SPIKE, 0, 0)),
    BEAR_TRAP("Bärenfalle", new SimpleImageRenderer("tiles/traps/baerenfalle.png", -124 / 2.15d, -100 / 4.55),
            new SimpleImageRenderer("tiles/traps/baerenfalle_aktiv.png", -124 / 2.15d, -100 / 4.55),
            new TrapImprintNodeMap(TrapIdConstants.T_BEAR_TRAP, 1, 1)),
    BEAR_TRAP_GHOST("Bärenfalle:Geist", VoidRenderer.get(), VoidRenderer.get(),
            new TrapImprintNodeMap(TrapIdConstants.T_BEAR_TRAP, 1, 1));

    private final String displayName;
    private final IRenderData normalRenderer;
    private final IRenderData triggeredRenderer;
    private final TrapImprintNodeMap nodeMap;

    TrapTile(final String displayName, final IRenderData normalRenderer, final IRenderData triggeredRenderer,
            TrapImprintNodeMap nodeMap) {
        this.displayName = displayName;
        this.normalRenderer = normalRenderer;
        this.triggeredRenderer = triggeredRenderer;
        this.nodeMap = nodeMap;
    }

    public String getDisplayName() {
        return displayName;
    }

    public IRenderData getNormalRenderer() {
        return normalRenderer;
    }

    public IRenderData getTriggeredRenderer() {
        return triggeredRenderer;
    }

    public TrapImprintNodeMap getNodeMap() {
        return nodeMap;
    }
}
